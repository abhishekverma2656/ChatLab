
package com.example.chatLab.chatLab.Controller;

import com.example.chatLab.chatLab.dto.Message.MessageRequestDTO;
import com.example.chatLab.chatLab.dto.Message.MessageResponseDTO;
import com.example.chatLab.chatLab.Entity.Message;
import com.example.chatLab.chatLab.Service.MessagesService;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketMessageController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessagesService messagesService;

    public WebSocketMessageController(
            SimpMessagingTemplate messagingTemplate,
            MessagesService messagesService) {
        this.messagingTemplate = messagingTemplate;
        this.messagesService = messagesService;
    }

    @MessageMapping("/chat.send")
    public void sendChatMessage(@Payload MessageRequestDTO request) {

        Message savedMessage = messagesService.sendMessage(
                request.getChatId(),
                request.getSenderId(),
                request.getContent(),
                request.getMessageType()
        );

        MessageResponseDTO response = MessageResponseDTO.builder()
                .id(savedMessage.getId())
                .chatId(savedMessage.getChat().getId())
                .senderId(savedMessage.getSender().getId())
                .content(savedMessage.getContent())
                .messageType(savedMessage.getMessageType())
                .createdAt(savedMessage.getCreatedAt())
                .build();

        messagingTemplate.convertAndSend(
                "/topic/chat/" + savedMessage.getChat().getId(),
                response
        );
    }
}
