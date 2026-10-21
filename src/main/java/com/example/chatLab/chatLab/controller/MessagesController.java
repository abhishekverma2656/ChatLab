
package com.example.chatLab.chatLab.Controller;

import com.example.chatLab.chatLab.Entity.Message;
import com.example.chatLab.chatLab.Service.MessagesService;
import com.example.chatLab.chatLab.dto.Message.MessageRequestDTO;
import com.example.chatLab.chatLab.dto.Message.MessageResponseDTO;
import com.example.chatLab.chatLab.mappers.MessageMapper;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessagesController {

    private final MessagesService messagesService;
    private final MessageMapper messageMapper;

    // Send Message
    @PostMapping("/chat/{chatId}/sender/{senderId}")
    public MessageResponseDTO sendMessage(
            @PathVariable Long chatId,
            @PathVariable Long senderId,
            @RequestBody MessageRequestDTO requestDTO) {

        Message message = messagesService.sendMessage(
                chatId,
                senderId,
                requestDTO.getContent(),
                requestDTO.getMessageType()
        );

        return messageMapper.toResponse(message);
    }

    // Get Chat Messages with Pagination
    @GetMapping("/chat/{chatId}")
    public Page<MessageResponseDTO> getChatMessages(
            @PathVariable Long chatId,
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<Message> messages = messagesService.getChatMessages(
                chatId, userId, page, size
        );

        return messages.map(messageMapper::toResponse);
    }

    // Delete Own Message
    @DeleteMapping("/{messageId}")
    public void deleteMessage(
            @PathVariable Long messageId,
            @RequestParam Long userId) {

        messagesService.deleteMessage(messageId, userId);
    }
}
