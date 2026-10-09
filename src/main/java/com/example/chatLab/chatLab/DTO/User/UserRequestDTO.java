package com.example.chatLab.chatLab.dto.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDTO {

    private String name;
    private String phoneNumber;
    private String email;
    private String password;
    private String profileImage;
    private String about;
}