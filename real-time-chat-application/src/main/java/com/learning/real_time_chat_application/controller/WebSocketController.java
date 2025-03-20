package com.learning.real_time_chat_application.controller;

import com.learning.real_time_chat_application.dto.request.MessageRequestDto;
import com.learning.real_time_chat_application.dto.response.MessageResponseDto;
import com.learning.real_time_chat_application.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private final MessageService messageService;


    @MessageMapping("/sendMessage")
    @SendTo("/topic/messages")
    public MessageResponseDto sendMessage(MessageRequestDto messageRequestDto) {
        return messageService.sendMessage(messageRequestDto);
    }

}
