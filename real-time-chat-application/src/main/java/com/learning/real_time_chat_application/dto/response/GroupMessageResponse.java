package com.learning.real_time_chat_application.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GroupMessageResponse {
    private Long groupId;
    private Long senderId;
    private String content;
    private LocalDateTime timestamp;
    private String senderName;
}
