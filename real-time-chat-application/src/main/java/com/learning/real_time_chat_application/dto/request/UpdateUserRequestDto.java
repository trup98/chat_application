package com.learning.real_time_chat_application.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UpdateUserRequestDto {
    private String userName;
    private String firstName;
    private String lastName;
}
