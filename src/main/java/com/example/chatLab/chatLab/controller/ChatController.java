
package com.example.chatLab.chatLab.Controller;


import com.example.chatLab.chatLab.dto.ChatListResponseDTO.ChatListResponseDTO;
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

        Chat chat = chatService.getChatById(chatId);

        return chatMapper.toResponse(chat);
    }

    // Get all chats of a user
    @GetMapping("/user/{userId}")
    public List<ChatResponseDTO> getUserChats(
            @PathVariable Long userId) {

        return chatService.getUserChats(userId)
                .stream()
                .map(chatMapper::toResponse)
                .toList();
    }

    // Check whether a user belongs to a chat
    @GetMapping("/{chatId}/participants/{userId}")
    public boolean isChatParticipant(
            @PathVariable Long chatId,
            @PathVariable Long userId) {

        return chatService.isChatParticipant(chatId, userId);
    }


    @PostMapping("/direct")
    public ChatResponseDTO createOrGetDirectChat(
            @RequestParam Long userId1,
            @RequestParam Long userId2) {

        Chat chat = chatService.createOrGetDirectChat(userId1, userId2);
        return chatMapper.toResponse(chat);
    }

    @GetMapping("/user/{userId}/list")
    public List<ChatListResponseDTO> getUserChatList(
            @PathVariable Long userId
    ) {
        return chatService.getUserChatList(userId);
    }

}
