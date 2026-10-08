package com.example.chatLab.chatLab.DTO.Contact;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContactResponseDTO {

    private Long id;
    private Long ownerId;
    private Long contactUserId;
    private String savedName;
    private LocalDateTime createdAt;
}