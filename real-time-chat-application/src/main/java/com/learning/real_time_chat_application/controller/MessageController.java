package com.learning.real_time_chat_application.controller;

import com.learning.real_time_chat_application.dto.request.EditMessageRequestDto;
import com.learning.real_time_chat_application.dto.request.MessageRequestDto;
import com.learning.real_time_chat_application.dto.response.ApiResponse;
import com.learning.real_time_chat_application.dto.response.MessageResponseDto;
import com.learning.real_time_chat_application.dto.response.SenderUnreadDto;
import com.learning.real_time_chat_application.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@CrossOrigin("http://localhost:3000")
public class MessageController {

    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping("/send")
    @MessageMapping("/chat")
    public ResponseEntity<ApiResponse> sendMessage(@RequestBody MessageRequestDto messageRequestDto) {
        MessageResponseDto messageResponseDto = messageService.sendMessage(messageRequestDto);
        messagingTemplate.convertAndSendToUser(messageResponseDto.getReceiverId().toString(), "/queue/messages", messageResponseDto);
        return new ResponseEntity<>(new ApiResponse(HttpStatus.OK, "Message Sent Successfully", messageResponseDto), HttpStatus.OK);
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse> historyMessages(@RequestParam Long senderId, @RequestParam Long receiverId) {
        List<MessageResponseDto> chatHistory = messageService.getChatHistory(senderId, receiverId);
        return new ResponseEntity<>(new ApiResponse(HttpStatus.OK, "Chat Found Successfully", chatHistory), HttpStatus.OK);
    }

    @DeleteMapping("/delete/conversation")
    public ResponseEntity<ApiResponse> deleteConversation(@RequestParam Long senderId, @RequestParam Long receiverId) {
        this.messageService.deleteConversation(senderId, receiverId);
        return new ResponseEntity<>(new ApiResponse(HttpStatus.OK, "Chat Deleted Successfully", Collections.emptyMap()), HttpStatus.OK);
    }

    @DeleteMapping("/unSend/message/{senderId}/{messageId}")
    public ResponseEntity<ApiResponse> unSendMessage(@PathVariable Long senderId, @PathVariable Long messageId) {
        this.messageService.unSendMessage(senderId, messageId);
        return new ResponseEntity<>(new ApiResponse(HttpStatus.OK, "Chat Deleted Successfully", Collections.emptyMap()), HttpStatus.OK);
    }

    @GetMapping("/unread-count/{receiverId}")
    public ResponseEntity<ApiResponse> getUnreadCount(@PathVariable Long receiverId) {
        List<SenderUnreadDto> unreadMessageCount = messageService.getUnreadMessageCount(receiverId);
        messagingTemplate.convertAndSendToUser(receiverId.toString(), "/queue/unread-count", unreadMessageCount);
        return new ResponseEntity<>(new ApiResponse(HttpStatus.OK, "Unread count fetched", unreadMessageCount), HttpStatus.OK);
    }

    @PutMapping("/mark-as-read")
    public ResponseEntity<ApiResponse> markMessagesAsRead(@RequestParam Long senderId, @RequestParam Long receiverId) {
        this.messageService.markMessagesAsRead(senderId, receiverId);
        return new ResponseEntity<>(new ApiResponse(HttpStatus.OK, "Messages marked as read", Collections.emptyMap()), HttpStatus.OK);
    }

    @PutMapping("/edit")
    public ResponseEntity<ApiResponse> editMessage(@RequestBody EditMessageRequestDto editMessageRequestDto) {
        this.messageService.editMessage(editMessageRequestDto);
        return new ResponseEntity<>(new ApiResponse(HttpStatus.OK, "Messages Edited Successfully", Collections.emptyMap()), HttpStatus.OK);
    }


}
