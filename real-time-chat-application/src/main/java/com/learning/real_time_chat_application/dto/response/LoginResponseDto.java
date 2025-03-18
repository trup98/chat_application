package com.learning.real_time_chat_application.dto.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponseDto {
    private String token;
    private String username;
    private String userRole;
}
