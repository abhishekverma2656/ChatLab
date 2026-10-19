
package com.example.chatLab.chatLab.Controller;


import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @PostMapping("/chat/{chatId}")
    public MessageResponseDTO sendMessage(
            @PathVariable Long chatId,
            @RequestBody MessageRequestDTO requestDTO) {

        Long currentUserId = getCurrentUserId();

        Message message = messagesService.sendMessage(
                chatId,
                currentUserId,
                requestDTO.getContent(),
                requestDTO.getMessageType()
        );

        return messageMapper.toResponse(message);
    }



// Get Chat Messages with Pagination
    @GetMapping("/chat/{chatId}")
    public Page<MessageResponseDTO> getChatMessages(
            @PathVariable Long chatId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Long currentUserId = getCurrentUserId();

        Page<Message> messages = messagesService.getChatMessages(
                chatId, currentUserId, page, size
        );

        return messages.map(messageMapper::toResponse);
    }


// Delete Own Message
    @DeleteMapping("/{messageId}")
    public void deleteMessage(
            @PathVariable Long messageId) {

        Long currentUserId = getCurrentUserId();

        messagesService.deleteMessage(messageId, currentUserId);
    }


    private Long getCurrentUserId() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        return Long.valueOf(authentication.getName());
    }
}
