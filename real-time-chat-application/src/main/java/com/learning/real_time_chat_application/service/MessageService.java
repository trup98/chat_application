package com.learning.real_time_chat_application.service;

import com.learning.real_time_chat_application.dto.request.MessageRequestDto;
import com.learning.real_time_chat_application.dto.response.MessageResponseDto;
import com.learning.real_time_chat_application.dto.response.SenderUnreadDto;

import java.util.List;

public interface MessageService {
    MessageResponseDto sendMessage(MessageRequestDto messageRequestDto);

    List<MessageResponseDto> getChatHistory(Long senderId, Long receiverId);

    void deleteConversation(Long senderId, Long receiverId);

    void unSendMessage(Long senderId, Long messageId);

    List<SenderUnreadDto> getUnreadMessageCount(Long receiverId);

    void markMessagesAsRead(Long senderId, Long receiverId);
}
