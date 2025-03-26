package com.learning.real_time_chat_application.service.impl;

import com.learning.real_time_chat_application.dto.request.RegisterUserRequestDto;
import com.learning.real_time_chat_application.dto.request.UpdateUserRequestDto;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
    public Page<GetAllUser> findAllUser(Pageable pageable, String searchKey, Long loggedInUserId) {
        var userPage = userRepository.findAllUser(pageable, searchKey, loggedInUserId).map(user -> modelMapper.map(user, GetAllUser.class));
        if (userPage.isEmpty()) {
            throw new CustomException(ExceptionEnum.USER_NOT_FOUND.getMessage(), HttpStatus.NOT_FOUND);
        }
        return userPage;
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

}
