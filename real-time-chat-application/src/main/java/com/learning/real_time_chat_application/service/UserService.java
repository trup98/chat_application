package com.learning.real_time_chat_application.service;

import com.learning.real_time_chat_application.dto.request.RegisterUserRequestDto;
import com.learning.real_time_chat_application.dto.request.UpdateUserRequestDto;
import com.learning.real_time_chat_application.dto.response.UserResponseDto;
import com.learning.real_time_chat_application.projection.GetAllUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    void addNewUser(RegisterUserRequestDto registerUserRequestDto);

    Page<GetAllUser> findAllUser(Pageable pageable, String searchKey);

    UserResponseDto getUserById(Long id);

    void deleteUser(Long id);

    void updateUser(Long id, UpdateUserRequestDto updateUserRequestDto);

}
