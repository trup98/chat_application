package com.learning.real_time_chat_application.service.impl;

import com.learning.real_time_chat_application.dto.request.MessageRequestDto;
import com.learning.real_time_chat_application.dto.response.MessageResponseDto;
import com.learning.real_time_chat_application.entity.ConversationEntity;
import com.learning.real_time_chat_application.entity.MessagesEntity;
import com.learning.real_time_chat_application.entity.UserEntity;
import com.learning.real_time_chat_application.enums.ExceptionEnum;
import com.learning.real_time_chat_application.exception.CustomException;
import com.learning.real_time_chat_application.repository.ConversationRepository;
import com.learning.real_time_chat_application.repository.MessageRepository;
import com.learning.real_time_chat_application.repository.UserRepository;
import com.learning.real_time_chat_application.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ConversationRepository conversationRepository;


    @Override
    public MessageResponseDto sendMessage(MessageRequestDto messageRequestDto) {
        UserEntity sender = userRepository.findById(messageRequestDto.getSenderId()).orElseThrow(() -> new CustomException(ExceptionEnum.SENDER_NOT_FOUND.getValue(), HttpStatus.NOT_FOUND));
        UserEntity receiver = userRepository.findById(messageRequestDto.getReceiverId()).orElseThrow(() -> new CustomException(ExceptionEnum.RECEIVER_NOT_FOUND.getValue(), HttpStatus.NOT_FOUND));

        ConversationEntity conversation = conversationRepository.findByUser1AndUser2(receiver, sender).
                orElseGet(() -> {
                    ConversationEntity conversationEntity = ConversationEntity.builder()
                            .user1(sender)
                            .user2(receiver)
                            .build();
                    return conversationRepository.save(conversationEntity);
                });

        MessagesEntity messagesEntity = MessagesEntity.builder()
                .senderId(sender)
                .receiverId(receiver)
                .timestamp(LocalDateTime.now())
                .content(messageRequestDto.getContent())
                .isRead(false)
                .conversation(conversation)
                .build();

        MessagesEntity savedMessage = messageRepository.save(messagesEntity);

        return MessageResponseDto.builder()
                .senderId(savedMessage.getSenderId().getId())
                .receiverId(savedMessage.getReceiverId().getId())
                .content(savedMessage.getContent())
                .timestamp(savedMessage.getTimestamp())
                .isRead(savedMessage.isRead())
                .conversationId(savedMessage.getConversation().getId())
                .build();

    }
}
