package com.learning.real_time_chat_application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SenderUnreadDto {
    private Long senderId;
    private Long unreadCount;
}
