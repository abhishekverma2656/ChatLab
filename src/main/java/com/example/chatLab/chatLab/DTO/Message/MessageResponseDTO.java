package com.example.chatLab.chatLab.DTO.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponseDTO {

    private Long id;
    private Long chatId;
    private Long senderId;
    private String content;
    private String messageType;
    private LocalDateTime createdAt;
}