package com.learning.real_time_chat_application.service;

import com.learning.real_time_chat_application.dto.request.MessageRequestDto;
import com.learning.real_time_chat_application.entity.ConversationEntity;
import com.learning.real_time_chat_application.entity.MessagesEntity;
import com.learning.real_time_chat_application.entity.UserEntity;

public interface ConversationService {
    ConversationEntity createOrFindExistingConversation(UserEntity sender, UserEntity receiver);

    MessagesEntity saveMessage(UserEntity sender, UserEntity receiver, MessageRequestDto messageRequestDto, ConversationEntity conversationEntity);

    ConversationEntity findConversationBetweenUsers(UserEntity senderUserId, UserEntity receiverUserId);
}
