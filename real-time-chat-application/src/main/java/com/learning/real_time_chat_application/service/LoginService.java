package com.learning.real_time_chat_application.service;


import com.learning.real_time_chat_application.dto.request.LoginRequestDto;
import com.learning.real_time_chat_application.dto.response.LoginResponseDto;

public interface LoginService {

    LoginResponseDto loginUser(LoginRequestDto loginRequestDto);


}
