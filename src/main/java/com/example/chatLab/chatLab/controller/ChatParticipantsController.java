
package com.example.chatLab.chatLab.Controller;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.example.chatLab.chatLab.Entity.ChatParticipants;
import com.example.chatLab.chatLab.Service.ChatParticipantsService;
import com.example.chatLab.chatLab.dto.ChatParticipants.ChatParticipantsResponseDTO;
import com.example.chatLab.chatLab.mappers.ChatParticipantsMapper;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chats/{chatId}/participants")
@RequiredArgsConstructor
public class ChatParticipantsController {

    private final ChatParticipantsService participantsService;
    private final ChatParticipantsMapper participantsMapper;

    // Get all participants
    @GetMapping
    public List<ChatParticipantsResponseDTO> getParticipants(
            @PathVariable Long chatId) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        Long currentUserId = Long.valueOf(authentication.getName());

        if (!participantsService.isParticipant(chatId, currentUserId)) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.FORBIDDEN,
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
            @PathVariable Long userId) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        Long currentUserId = Long.valueOf(authentication.getName());

        if (!participantsService.isParticipant(chatId, currentUserId)) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.FORBIDDEN,
                    "Only chat participants can add members"
            );
        }

        ChatParticipants participant =
                participantsService.addParticipant(chatId, userId);

        return participantsMapper.toResponse(participant);
    }


    // Remove participant from group

    @DeleteMapping("/{userId}")
    public void removeParticipant(
            @PathVariable Long chatId,
            @PathVariable Long userId) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        Long currentUserId = Long.valueOf(authentication.getName());

        if (!currentUserId.equals(userId)) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.FORBIDDEN,
                    "You can only remove yourself from a group"
            );
        }

        participantsService.removeParticipant(chatId, currentUserId);
    }


    // Update last-read message

    @PutMapping("/{userId}/last-read/{messageId}")
    public ChatParticipantsResponseDTO updateLastReadMessage(
            @PathVariable Long chatId,
            @PathVariable Long userId,
            @PathVariable Long messageId) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        Long currentUserId = Long.valueOf(authentication.getName());

        if (!currentUserId.equals(userId)) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.FORBIDDEN,
                    "You can only update your own read status"
            );
        }

        ChatParticipants participant =
                participantsService.updateLastReadMessage(
                        chatId, currentUserId, messageId
                );

        return participantsMapper.toResponse(participant);
    }

}
