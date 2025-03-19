package com.learning.real_time_chat_application.controller;

import com.learning.real_time_chat_application.dto.request.MessageRequestDto;
import com.learning.real_time_chat_application.dto.response.ApiResponse;
import com.learning.real_time_chat_application.dto.response.MessageResponseDto;
import com.learning.real_time_chat_application.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping("/send")
    public ResponseEntity<ApiResponse> sendMessage(@RequestBody MessageRequestDto messageRequestDto) {
        MessageResponseDto messageResponseDto = messageService.sendMessage(messageRequestDto);
        return new ResponseEntity<>(new ApiResponse(HttpStatus.OK, "Message Sent Successfully", messageResponseDto), HttpStatus.OK);
    }

}
