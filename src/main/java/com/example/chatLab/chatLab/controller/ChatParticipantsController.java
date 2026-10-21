
package com.example.chatLab.chatLab.Controller;

import com.example.chatLab.chatLab.Entity.ChatParticipants;
import com.example.chatLab.chatLab.Service.ChatParticipantsService;
import com.example.chatLab.chatLab.dto.ChatParticipants.ChatParticipantsResponseDTO;
import com.example.chatLab.chatLab.mappers.ChatParticipantsMapper;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/chats/{chatId}/participants")
@RequiredArgsConstructor
public class ChatParticipantsController {

    private final ChatParticipantsService participantsService;
    private final ChatParticipantsMapper participantsMapper;

    // Get authenticated user's ID from Spring Security
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

    // Get all participants
    @GetMapping
    public List<ChatParticipantsResponseDTO> getParticipants(
            @PathVariable Long chatId
    ) {
        Long currentUserId = getCurrentUserId();

        if (!participantsService.isParticipant(chatId, currentUserId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You cannot access participants of this chat"
            );
        }

        return participantsService.getParticipantsByChatId(chatId)
                .stream()
                .map(participantsMapper::toResponse)
                .toList();
    }

    // Add participant to group
    @PostMapping("/{userId}")
    public ChatParticipantsResponseDTO addParticipant(
            @PathVariable Long chatId,
            @PathVariable Long userId
    ) {
        Long currentUserId = getCurrentUserId();

        if (!participantsService.isParticipant(chatId, currentUserId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only chat participants can add members"
            );
        }

        ChatParticipants participant =
                participantsService.addParticipant(chatId, userId);

        return participantsMapper.toResponse(participant);
    }

    // Remove yourself from a group
    @DeleteMapping("/{userId}")
    public void removeParticipant(
            @PathVariable Long chatId,
            @PathVariable Long userId
    ) {
        Long currentUserId = getCurrentUserId();

        if (!currentUserId.equals(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You can only remove yourself from a group"
            );
        }

        if (!participantsService.isParticipant(chatId, currentUserId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not a participant of this chat"
            );
        }

        participantsService.removeParticipant(chatId, currentUserId);
    }

    // Update last-read message
    @PutMapping("/{userId}/last-read/{messageId}")
    public ChatParticipantsResponseDTO updateLastReadMessage(
            @PathVariable Long chatId,
            @PathVariable Long userId,
            @PathVariable Long messageId
    ) {
        Long currentUserId = getCurrentUserId();

        if (!currentUserId.equals(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You can only update your own read status"
            );
        }

        if (!participantsService.isParticipant(chatId, currentUserId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not a participant of this chat"
            );
        }

        ChatParticipants participant =
                participantsService.updateLastReadMessage(
                        chatId,
                        currentUserId,
                        messageId
                );

        return participantsMapper.toResponse(participant);
    }
}
