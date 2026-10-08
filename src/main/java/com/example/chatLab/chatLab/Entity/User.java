package com.example.chatLab.chatLab.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

   @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "phone_number", unique = true, nullable = false)
    private String phoneNumber;

    @Column(unique = true)
    private String email;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "profile_image")
    private String profileImage;

    private String about;

    @Column(name = "last_seen")
    private LocalDateTime lastSeen;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    @OneToMany(mappedBy = "owner")
    private List<Contact> ownedContacts;


    @OneToMany(mappedBy = "contactUser")
    private List<Contact> contactOf;

    @OneToMany(mappedBy = "user")
    private List<ChatParticipants> chatParticipants;


    @OneToMany(mappedBy = "sender")
    private List<Messages> messages;
}
