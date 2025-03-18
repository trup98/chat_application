package com.learning.real_time_chat_application.dto.request;

import lombok.Data;

@Data
public class LoginRequestDto {

    private String email;
    private String password;
}
