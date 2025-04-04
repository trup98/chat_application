package com.learning.real_time_chat_application.service;

import com.learning.real_time_chat_application.dto.request.RegisterUserRequestDto;
import com.learning.real_time_chat_application.dto.request.UpdateUserRequestDto;
import com.learning.real_time_chat_application.dto.response.GetAllUserDto;
import com.learning.real_time_chat_application.dto.response.UserProfileResponseDTO;
import com.learning.real_time_chat_application.dto.response.UserResponseDto;
import com.learning.real_time_chat_application.projection.GetAllUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserService {
    void addNewUser(RegisterUserRequestDto registerUserRequestDto);

    Page<GetAllUserDto> findAllUser(Pageable pageable, String searchKey, Long loggedInUserId);

    UserResponseDto getUserById(Long id);

    void deleteUser(Long id);

    void updateUser(Long id, UpdateUserRequestDto updateUserRequestDto);

    UserProfileResponseDTO setUserProfile(MultipartFile file, Long id) throws IOException;

    void removeProfilePicture(Long id);
}
