package com.example.chatLab.chatLab.DTO.ChatParticipants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatParticipantsResponseDTO {

    private Long id;
    private Long chatId;
    private Long userId;
    private Long lastReadMessageId;
    private LocalDateTime joinedAt;
}