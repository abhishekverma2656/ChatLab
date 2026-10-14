
package com.example.chatLab.chatLab.Controller;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.example.chatLab.chatLab.Entity.Chat;
import com.example.chatLab.chatLab.Service.ChatService;
import com.example.chatLab.chatLab.dto.Chat.ChatResponseDTO;
import com.example.chatLab.chatLab.mappers.ChatMapper;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final ChatMapper chatMapper;

    // Get chat by ID
    @GetMapping("/{chatId}")
    public ChatResponseDTO getChatById(
            @PathVariable Long chatId) {

        Long currentUserId = getCurrentUserId();

        if (!chatService.isChatParticipant(chatId, currentUserId)) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.FORBIDDEN,
                    "You cannot access this chat"
            );
        }

        Chat chat = chatService.getChatById(chatId);

        return chatMapper.toResponse(chat);
    }


    // Get all chats of a user
    @GetMapping("/user/{userId}")
    public List<ChatResponseDTO> getUserChats(
            @PathVariable Long userId) {

        Long currentUserId = getCurrentUserId();

        if (!currentUserId.equals(userId)) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.FORBIDDEN,
                    "You cannot access another user's chats"
            );
        }

        return chatService.getUserChats(currentUserId)
                .stream()
                .map(chatMapper::toResponse)
                .toList();
    }


// Check whether the current user belongs to a chat
    @GetMapping("/{chatId}/participants/{userId}")
    public boolean isChatParticipant(
            @PathVariable Long chatId,
            @PathVariable Long userId) {

        Long currentUserId = getCurrentUserId();

        if (!currentUserId.equals(userId)) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.FORBIDDEN,
                    "You cannot check another user's membership"
            );
        }

        return chatService.isChatParticipant(chatId, currentUserId);
    }




    @PostMapping("/direct")
    public ChatResponseDTO createOrGetDirectChat(
            @RequestParam Long otherUserId) {

        Long currentUserId = getCurrentUserId();

        Chat chat = chatService.createOrGetDirectChat(
                currentUserId,
                otherUserId
        );

        return chatMapper.toResponse(chat);
    }






    private Long getCurrentUserId() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        return Long.valueOf(authentication.getName());
    }

}
