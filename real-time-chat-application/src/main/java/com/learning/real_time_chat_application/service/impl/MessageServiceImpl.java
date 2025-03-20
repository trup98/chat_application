package com.learning.real_time_chat_application.service.impl;

import com.learning.real_time_chat_application.dto.request.MessageRequestDto;
import com.learning.real_time_chat_application.dto.response.MessageResponseDto;
import com.learning.real_time_chat_application.entity.ConversationEntity;
import com.learning.real_time_chat_application.entity.MessagesEntity;
import com.learning.real_time_chat_application.entity.UserEntity;
import com.learning.real_time_chat_application.enums.ExceptionEnum;
import com.learning.real_time_chat_application.exception.CustomException;
import com.learning.real_time_chat_application.repository.MessageRepository;
import com.learning.real_time_chat_application.repository.UserRepository;
import com.learning.real_time_chat_application.service.ConversationService;
import com.learning.real_time_chat_application.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final UserRepository userRepository;
    private final ConversationService conversationService;
    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public MessageResponseDto sendMessage(MessageRequestDto messageRequestDto) {

        UserEntity sender = userRepository.findById(messageRequestDto.getSenderId()).orElseThrow(() -> new CustomException(ExceptionEnum.SENDER_NOT_FOUND.getMessage(), HttpStatus.NOT_FOUND));
        UserEntity receiver = userRepository.findById(messageRequestDto.getReceiverId()).orElseThrow(() -> new CustomException(ExceptionEnum.RECEIVER_NOT_FOUND.getMessage(), HttpStatus.NOT_FOUND));

        ConversationEntity conversationEntity = this.conversationService.createOrFindExistingConversation(sender, receiver);

        var savedMessage = this.conversationService.saveMessage(sender, receiver, messageRequestDto, conversationEntity);

        MessageResponseDto messageResponseDto = MessageResponseDto.builder()
                .senderName(savedMessage.getSenderId().getUserName())
                .receiverName(savedMessage.getReceiverId().getUserName())
                .senderId(savedMessage.getSenderId().getId())
                .receiverId(savedMessage.getReceiverId().getId())
                .content(savedMessage.getContent())
                .timestamp(savedMessage.getTimestamp())
                .isRead(savedMessage.isRead())
                .conversationId(savedMessage.getConversation().getId())
                .build();

        simpMessagingTemplate.convertAndSend("/topic/messages", messageResponseDto);
        return messageResponseDto;
    }

    @Override
    public List<MessageResponseDto> getChatHistory(Long senderId, Long receiverId) {

        UserEntity senderUserId = this.userRepository.findById(senderId).orElseThrow(() -> new CustomException(ExceptionEnum.SENDER_NOT_FOUND.getMessage(), HttpStatus.NOT_FOUND));
        UserEntity receiverUserId = this.userRepository.findById(receiverId).orElseThrow(() -> new CustomException(ExceptionEnum.RECEIVER_NOT_FOUND.getMessage(), HttpStatus.NOT_FOUND));

        ConversationEntity conversationBetweenUsers = conversationService.findConversationBetweenUsers(senderUserId, receiverUserId);

        List<MessagesEntity> messages = messageRepository.findByConversationId(conversationBetweenUsers.getId());

        return messages.stream().
                map(message -> MessageResponseDto.builder()
                        .senderName(message.getSenderId().getUserName())
                        .receiverName(message.getReceiverId().getUserName())
                        .senderId(message.getSenderId().getId())
                        .receiverId(message.getReceiverId().getId())
                        .content(message.getContent())
                        .timestamp(message.getTimestamp())
                        .isRead(message.isRead())
                        .conversationId(conversationBetweenUsers.getId())
                        .build())
                .collect(Collectors.toList());
    }
}
