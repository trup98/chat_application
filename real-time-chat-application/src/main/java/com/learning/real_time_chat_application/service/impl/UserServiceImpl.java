package com.learning.real_time_chat_application.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.learning.real_time_chat_application.dto.request.RegisterUserRequestDto;
import com.learning.real_time_chat_application.dto.request.UpdateUserRequestDto;
import com.learning.real_time_chat_application.dto.response.GetAllUserDto;
import com.learning.real_time_chat_application.dto.response.UserProfileResponseDTO;
import com.learning.real_time_chat_application.dto.response.UserResponseDto;
import com.learning.real_time_chat_application.entity.RoleEntity;
import com.learning.real_time_chat_application.entity.UserEntity;
import com.learning.real_time_chat_application.entity.UserRoleMappingEntity;
import com.learning.real_time_chat_application.enums.ExceptionEnum;
import com.learning.real_time_chat_application.exception.CustomException;
import com.learning.real_time_chat_application.projection.GetAllUser;
import com.learning.real_time_chat_application.repository.RoleRepository;
import com.learning.real_time_chat_application.repository.UserRepository;
import com.learning.real_time_chat_application.repository.UserRoleMappingRepository;
import com.learning.real_time_chat_application.service.UserService;
import com.learning.real_time_chat_application.utill.Utilities;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserRoleMappingRepository userRoleMappingRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final Utilities utilities;
    private final RoleRepository roleRepository;
    private final AmazonS3 amazonS3;
    private final S3Service s3Service;


    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    @Override
    public void addNewUser(RegisterUserRequestDto registerUserRequestDto) {


        var user = userRepository.findByEmail(registerUserRequestDto.getEmail());
        if (user.isPresent()) {
            throw new CustomException(ExceptionEnum.USER_EXISTS.getMessage(), HttpStatus.NOT_ACCEPTABLE);
        }

        UserEntity currentUser = utilities.currentUser();


        UserEntity saveUser = new UserEntity();
        saveUser.setEmail(registerUserRequestDto.getEmail());
        saveUser.setPassword(passwordEncoder.encode(registerUserRequestDto.getPassword()));
        saveUser.setUserName(registerUserRequestDto.getUserName().trim());
        saveUser.setCreatedBy(currentUser);
        saveUser.setUpdatedBy(currentUser);
        UserEntity savedUser = this.userRepository.save(saveUser);
        Long defaultRoleId = 2L;
        RoleEntity roleEntity = this.roleRepository.findById(defaultRoleId)
                .orElseThrow(() -> new CustomException(ExceptionEnum.ROLE_NOT_FOUND.getValue(), HttpStatus.NOT_FOUND));

        UserRoleMappingEntity userRoleMappingEntity = new UserRoleMappingEntity();
        userRoleMappingEntity.setRoleId(roleEntity);
        userRoleMappingEntity.setUserId(savedUser);
        userRoleMappingEntity.setCreatedBy(currentUser);
        userRoleMappingEntity.setUpdatedBy(currentUser);
        this.userRoleMappingRepository.save(userRoleMappingEntity);
    }

    @Override
    public UserProfileResponseDTO setUserProfile(MultipartFile file, Long id) throws IOException {
        // Validate file size (10 MB)
        long fileSize = 10 * 1024 * 1024;
        if (file.getSize() > fileSize) {
            throw new CustomException(ExceptionEnum.FILE_SIZE_EXCEEDED.getMessage(), HttpStatus.PAYLOAD_TOO_LARGE);
        }

        // Validate file type
        List<String> allowedFileTypes = Arrays.asList("image/jpeg", "image/png", "application/pdf", "text/plain");
        String fileContentType = file.getContentType();
        if (!allowedFileTypes.contains(fileContentType)) {
            throw new CustomException(ExceptionEnum.INVALID_FILE_TYPE.getMessage(), HttpStatus.NOT_ACCEPTABLE);
        }

        // Generate a unique filename
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        amazonS3.putObject(bucketName, fileName, file.getInputStream(), null);

        // Upload the file to S3
        UserEntity currentUser = utilities.currentUser();


        UserEntity userEntity = this.userRepository.findById(id).orElseThrow(() -> new CustomException(ExceptionEnum.USER_NOT_FOUND.getValue(), HttpStatus.NOT_FOUND));
        userEntity.setCreatedBy(currentUser);
        userEntity.setUpdatedBy(currentUser);
        userEntity.setUserProfileS3Url(fileName);
        UserEntity user = this.userRepository.save(userEntity);

        return new UserProfileResponseDTO(user.getId(), user.getUserProfileS3Url());
    }

    @Override
    public Page<GetAllUserDto> findAllUser(Pageable pageable, String searchKey, Long loggedInUserId) {


        Page<GetAllUser> userPage = userRepository.findAllUser(pageable, searchKey, loggedInUserId);

        if (userPage.isEmpty()) {
            throw new CustomException(ExceptionEnum.USER_NOT_FOUND.getMessage(), HttpStatus.NOT_FOUND);
        }

        // Convert projection results to DTOs and generate pre-signed URLs
        List<GetAllUserDto> userDtoList = userPage.stream().map(user -> {
            String preSignedUrl = null;

            // Generate a pre-signed URL only if the profile picture exists
            if (user.getUserProfileS3Url() != null && !user.getUserProfileS3Url().isEmpty()) {
                preSignedUrl = s3Service.generatePreSignedUrl(user.getUserProfileS3Url());
            }

            return new GetAllUserDto(user.getId(), user.getUserName(), user.getEmail(), preSignedUrl);
        }).collect(Collectors.toList());

        return new PageImpl<>(userDtoList, pageable, userPage.getTotalElements());
    }

    @Override
    public UserResponseDto getUserById(Long id) {
        return userRepository.findByIdAndIsActiveTrue(id)
                .map(user -> modelMapper.map(user, UserResponseDto.class))
                .orElseThrow(() -> {
                    log.error("User not found with ID {}", id);
                    return new CustomException(ExceptionEnum.USER_NOT_FOUND.getMessage(), HttpStatus.NOT_FOUND);
                });
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.findById(id).ifPresentOrElse(
                user -> {
                    user.setIsActive(false);
                    user.setIsDeleted(true);
                    userRepository.save(user);
                },
                () -> {
                    throw new CustomException(ExceptionEnum.USER_NOT_FOUND.getMessage(), HttpStatus.NOT_FOUND);
                }
        );
    }

    @Override
    public void updateUser(Long id, UpdateUserRequestDto updateUserRequestDto) {
        UserEntity currentUser = utilities.currentUser();
        UserEntity updateUser = userRepository.findByIdAndIsActiveTrue(id).orElseThrow(() ->
                new CustomException(ExceptionEnum.USER_NOT_FOUND.getMessage(), HttpStatus.NOT_FOUND));
        updateUser.setUserName(updateUserRequestDto.getUserName());
        updateUser.setUpdatedBy(currentUser);
        this.userRepository.save(updateUser);
    }

    @Override
    public void removeProfilePicture(Long id) {
        UserEntity userEntity = this.userRepository.findById(id).orElseThrow(() -> new CustomException(ExceptionEnum.USER_NOT_FOUND.getValue(), HttpStatus.NOT_FOUND));
        String userProfileS3Url = userEntity.getUserProfileS3Url();

        if (userProfileS3Url != null && !userProfileS3Url.isEmpty()) {
            s3Service.deleteFileFromS3(userProfileS3Url);
            userEntity.setUserProfileS3Url(null);
            this.userRepository.save(userEntity);
        }
    }
}
