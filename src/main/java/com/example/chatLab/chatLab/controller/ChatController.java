
package com.example.chatLab.chatLab.Controller;

import com.example.chatLab.chatLab.Entity.Chat;
import com.example.chatLab.chatLab.Service.ChatService;
import com.example.chatLab.chatLab.dto.Chat.ChatResponseDTO;
import com.example.chatLab.chatLab.dto.ChatListResponseDTO.ChatListResponseDTO;
import com.example.chatLab.chatLab.mappers.ChatMapper;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
            @PathVariable Long chatId
    ) {
        Long currentUserId = getCurrentUserId();

        if (!chatService.isChatParticipant(chatId, currentUserId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You cannot access this chat"
            );
        }

        Chat chat = chatService.getChatById(chatId);

        return chatMapper.toResponse(chat);
    }

    // Get all chats of the authenticated user
    @GetMapping("/user/{userId}")
    public List<ChatResponseDTO> getUserChats(
            @PathVariable Long userId
    ) {
        Long currentUserId = getCurrentUserId();

        if (!currentUserId.equals(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You cannot access another user's chats"
            );
        }

        return chatService.getUserChats(currentUserId)
                .stream()
                .map(chatMapper::toResponse)
                .toList();
    }

    // Check whether the authenticated user belongs to a chat
    @GetMapping("/{chatId}/participants/{userId}")
    public boolean isChatParticipant(
            @PathVariable Long chatId,
            @PathVariable Long userId
    ) {
        Long currentUserId = getCurrentUserId();

        if (!currentUserId.equals(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You cannot check another user's membership"
            );
        }

        return chatService.isChatParticipant(chatId, currentUserId);
    }

    // Create or get a direct chat
    @PostMapping("/direct")
    public ChatResponseDTO createOrGetDirectChat(
            @RequestParam Long otherUserId
    ) {
        Long currentUserId = getCurrentUserId();

        Chat chat = chatService.createOrGetDirectChat(
                currentUserId,
                otherUserId
        );

        return chatMapper.toResponse(chat);
    }

    // Get chat list for the authenticated user
    @GetMapping("/user/{userId}/list")
    public List<ChatListResponseDTO> getUserChatList(
            @PathVariable Long userId
    ) {
        Long currentUserId = getCurrentUserId();

        if (!currentUserId.equals(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You cannot access another user's chat list"
            );
        }

        return chatService.getUserChatList(currentUserId);
    }

    // Get authenticated user's ID from Spring Security context
    private Long getCurrentUserId() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getName() == null
                || authentication.getName().equals("anonymousUser")) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "User is not authenticated"
            );
        }

        try {
            return Long.valueOf(authentication.getName());
        } catch (NumberFormatException exception) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid authenticated user ID"
            );
        }
    }
}
