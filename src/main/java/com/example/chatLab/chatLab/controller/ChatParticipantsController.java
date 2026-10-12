
package com.example.chatLab.chatLab.controller;

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

        ChatParticipants participant =
                participantsService.addParticipant(chatId, userId);

        return participantsMapper.toResponse(participant);
    }

    // Remove participant from group
    @DeleteMapping("/{userId}")
    public void removeParticipant(
            @PathVariable Long chatId,
            @PathVariable Long userId) {

        participantsService.removeParticipant(chatId, userId);
    }

    // Update last-read message
    @PutMapping("/{userId}/last-read/{messageId}")
    public ChatParticipantsResponseDTO updateLastReadMessage(
            @PathVariable Long chatId,
            @PathVariable Long userId,
            @PathVariable Long messageId) {

        ChatParticipants participant =
                participantsService.updateLastReadMessage(
                        chatId, userId, messageId);

        return participantsMapper.toResponse(participant);
    }
}
