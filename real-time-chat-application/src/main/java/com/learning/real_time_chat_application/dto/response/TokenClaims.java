package com.learning.real_time_chat_application.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TokenClaims {

    private String token;
    private String userName;
    private Long userId;
    private String userRole;
}