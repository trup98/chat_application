package com.learning.real_time_chat_application.service;

import com.learning.real_time_chat_application.dto.request.MessageRequestDto;
import com.learning.real_time_chat_application.dto.response.MessageResponseDto;

public interface MessageService {
    MessageResponseDto sendMessage(MessageRequestDto messageRequestDto);
}
