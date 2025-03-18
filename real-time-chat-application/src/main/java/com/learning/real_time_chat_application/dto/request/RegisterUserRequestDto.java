package com.learning.real_time_chat_application.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RegisterUserRequestDto {

    private String userName;
    private String password;
    private String email;

}
