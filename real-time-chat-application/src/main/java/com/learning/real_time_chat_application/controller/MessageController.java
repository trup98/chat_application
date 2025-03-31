package com.learning.real_time_chat_application.controller;

import com.learning.real_time_chat_application.dto.request.MessageRequestDto;
import com.learning.real_time_chat_application.dto.response.ApiResponse;
import com.learning.real_time_chat_application.dto.response.MessageResponseDto;
import com.learning.real_time_chat_application.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

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


}
