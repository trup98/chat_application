package com.learning.real_time_chat_application.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageResponseDto {
    private String senderName;
    private String receiverName;
    private Long senderId;
    private Long receiverId;
    private String content;
    private LocalDateTime timestamp;
    private boolean isRead;
    private Long conversationId;
}
