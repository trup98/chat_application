package com.learning.real_time_chat_application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserAvailableDTO {
    private Long userId;
    private String userName;
}
