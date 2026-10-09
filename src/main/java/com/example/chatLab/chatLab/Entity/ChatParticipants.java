package com.example.chatLab.chatLab.Entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_participants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatParticipants {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    private Long lastReadMessageId;


    @Column(name = "joined_at")
    private LocalDateTime joinedAt;
}
