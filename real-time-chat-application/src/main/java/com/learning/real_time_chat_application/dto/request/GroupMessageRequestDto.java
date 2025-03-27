package com.learning.real_time_chat_application.dto.request;

import lombok.Data;

@Data
public class GroupMessageRequestDto {
    private Long groupId;
    private Long senderId;
    private String content;
}
