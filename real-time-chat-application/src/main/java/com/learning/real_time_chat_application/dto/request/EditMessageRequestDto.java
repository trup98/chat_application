package com.learning.real_time_chat_application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditMessageRequestDto {

    private Long senderId;
    private Long messageId;
    private String newMessageContent;
}
