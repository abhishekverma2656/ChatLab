package com.example.chatLab.chatLab.dto.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageRequestDTO {

    private Long chatId;
    private Long senderId;
    private String content;
    private String messageType;
}