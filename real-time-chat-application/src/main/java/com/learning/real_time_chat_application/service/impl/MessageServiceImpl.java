package com.learning.real_time_chat_application.service.impl;

import com.learning.real_time_chat_application.dto.request.EditMessageRequestDto;
import com.learning.real_time_chat_application.dto.request.MessageRequestDto;
import com.learning.real_time_chat_application.dto.response.MessageResponseDto;
import com.learning.real_time_chat_application.dto.response.SenderUnreadDto;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
                .id(savedMessage.getId())
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

//        List<MessagesEntity> messages = messageRepository.findByConversationIdSorted(conversationBetweenUsers.getId());

        List<MessagesEntity> messages = messageRepository.findVisibleMessages(conversationBetweenUsers.getId(), senderId);

        return messages.stream().
                map(message -> MessageResponseDto.builder()
                        .id(message.getId())
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

    @Override
    public void deleteConversation(Long senderId, Long receiverId) {
        UserEntity sender = this.userRepository.findById(senderId)
                .orElseThrow(() -> new CustomException(ExceptionEnum.SENDER_NOT_FOUND.getMessage(), HttpStatus.NOT_FOUND));
        UserEntity receiver = this.userRepository.findById(receiverId)
                .orElseThrow(() -> new CustomException(ExceptionEnum.RECEIVER_NOT_FOUND.getMessage(), HttpStatus.NOT_FOUND));

        ConversationEntity conversation = this.conversationService.findConversationBetweenUsers(sender, receiver);
        List<MessagesEntity> messages = this.messageRepository.findByConversationIdSorted(conversation.getId());

        for (MessagesEntity message : messages) {
            if (message.getSenderId().getId().equals(senderId)) {
                message.setIsDeletedForSender(true);
            } else if (message.getReceiverId().getId().equals(senderId)) {
                message.setIsDeletedForReceiver(true);
            }

            this.messageRepository.save(message);
        }
    }

    @Override
    public void unSendMessage(Long senderId, Long messageId) {
        UserEntity userEntity = this.userRepository.findById(senderId).orElseThrow(() -> new CustomException(ExceptionEnum.SENDER_NOT_FOUND.getMessage(), HttpStatus.NOT_FOUND));
        MessagesEntity messagesEntity = this.messageRepository.findById(messageId).orElseThrow(() -> new CustomException(ExceptionEnum.MESSAGE_NOT_FOUND.getMessage(), HttpStatus.NOT_FOUND));

        if (!messagesEntity.getSenderId().getId().equals(userEntity.getId())) {
            // If the user is not the sender
            throw new CustomException(ExceptionEnum.YOU_CAN_ONLY_UNSEND_YOUR_OWN_MESSAGES.getMessage(), HttpStatus.FORBIDDEN);
        }
        messagesEntity.setIsActive(false);
        messagesEntity.setIsDeleted(true);
        this.messageRepository.save(messagesEntity);

    }

    @Override
    public List<SenderUnreadDto> getUnreadMessageCount(Long receiverId) {
        return messageRepository.findUnreadCountsBySender(receiverId);
    }

    @Override
    public void markMessagesAsRead(Long senderId, Long receiverId) {
        List<MessagesEntity> unreadMessages = messageRepository
                .findBySenderId_IdAndReceiverId_IdAndIsReadFalseAndIsDeletedFalse(senderId, receiverId);

        for (MessagesEntity message : unreadMessages) {
            message.setRead(true);
        }

        messageRepository.saveAll(unreadMessages);
    }

    @Override
    public void editMessage(EditMessageRequestDto editMessageRequestDto) {
        UserEntity userEntity = this.userRepository.findById(editMessageRequestDto.getSenderId()).orElseThrow(() -> new CustomException(ExceptionEnum.SENDER_NOT_FOUND.getMessage(), HttpStatus.NOT_FOUND));
        MessagesEntity messagesEntity = this.messageRepository.findById(editMessageRequestDto.getMessageId()).orElseThrow(() -> new CustomException(ExceptionEnum.MESSAGE_NOT_FOUND.getMessage(), HttpStatus.NOT_FOUND));

        if (!messagesEntity.getSenderId().getId().equals(userEntity.getId())) {
            throw new CustomException(ExceptionEnum.YOU_CAN_NOT_EDIT_THIS_MESSAGE.getMessage(), HttpStatus.FORBIDDEN);
        }
        messagesEntity.setContent(editMessageRequestDto.getNewMessageContent());
        messagesEntity.setEditedAt(LocalDateTime.now());
        messagesEntity.setIsEdited(true);
        this.messageRepository.save(messagesEntity);
    }
}
