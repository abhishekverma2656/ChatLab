package com.example.chatLab.chatLab.DTO.Contact;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContactRequestDTO {

    private Long contactUserId;
    private String savedName;
}