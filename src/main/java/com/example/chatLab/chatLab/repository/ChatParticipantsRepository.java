package com.example.chatLab.chatLab.repository;

import com.example.chatLab.chatLab.Entity.ChatParticipants;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatParticipantsRepository extends JpaRepository<ChatParticipants,Long> {
}
