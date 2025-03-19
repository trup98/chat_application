package com.learning.real_time_chat_application.dto.request;

import lombok.Data;


@Data
public class MessageRequestDto {
    private Long senderId;
    private Long receiverId;
    private String content;
}
