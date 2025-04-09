package com.learning.real_time_chat_application.service.impl;

import com.learning.real_time_chat_application.dto.request.MessageRequestDto;
import com.learning.real_time_chat_application.entity.ConversationEntity;
import com.learning.real_time_chat_application.entity.MessagesEntity;
import com.learning.real_time_chat_application.entity.UserEntity;
import com.learning.real_time_chat_application.enums.ExceptionEnum;
import com.learning.real_time_chat_application.exception.CustomException;
import com.learning.real_time_chat_application.repository.ConversationRepository;
import com.learning.real_time_chat_application.repository.MessageRepository;
import com.learning.real_time_chat_application.service.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ConversationServiceImpl implements ConversationService {
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;

    @Override
    public ConversationEntity createOrFindExistingConversation(UserEntity sender, UserEntity receiver) {
        Optional<ConversationEntity> conversationEntityOptional = this.conversationRepository.findByUser1AndUser2OrUser2AndUser1(sender, receiver, sender, receiver);

        if (conversationEntityOptional.isPresent()) {
            return conversationEntityOptional.get();
        } else {
            ConversationEntity newConversation = ConversationEntity.builder()
                    .user1(sender)
                    .user2(receiver)
                    .build();
            return this.conversationRepository.save(newConversation);
        }
    }

    @Override
    public MessagesEntity saveMessage(UserEntity sender, UserEntity receiver, MessageRequestDto messageRequestDto, ConversationEntity conversationEntity) {

        MessagesEntity messagesEntity = new MessagesEntity();
        messagesEntity.setSenderId(sender);
        messagesEntity.setReceiverId(receiver);
        messagesEntity.setConversation(conversationEntity);
        messagesEntity.setContent(messageRequestDto.getContent());
        messagesEntity.setRead(false);
        messagesEntity.setTimestamp(LocalDateTime.now());
        return this.messageRepository.save(messagesEntity);
    }

    @Override
    public ConversationEntity findConversationBetweenUsers(UserEntity senderUserId, UserEntity receiverUserId) {
        return this.conversationRepository.findByUser1AndUser2(senderUserId, receiverUserId).orElseGet(() -> conversationRepository.findByUser1AndUser2(receiverUserId, senderUserId)
                .orElseThrow(() -> new CustomException(ExceptionEnum.CONVERSATION_NOT_FOUND.getMessage(), HttpStatus.NOT_FOUND))
        );
    }

}
