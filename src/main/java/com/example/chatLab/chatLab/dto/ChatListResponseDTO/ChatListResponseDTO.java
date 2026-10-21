
package com.example.chatLab.chatLab.dto.ChatListResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatListResponseDTO {

    private Long chatId;

    private String type;

    private Long participantId;

    private String participantName;

    private String lastMessage;

    private LocalDateTime lastMessageAt;
}
