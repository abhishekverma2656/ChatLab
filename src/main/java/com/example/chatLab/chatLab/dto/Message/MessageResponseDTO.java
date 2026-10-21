package com.example.chatLab.chatLab.dto.Message;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageResponseDTO {

    private Long id;
    private Long chatId;
    private Long senderId;
    private String content;
    private String messageType;
    private LocalDateTime createdAt;
}