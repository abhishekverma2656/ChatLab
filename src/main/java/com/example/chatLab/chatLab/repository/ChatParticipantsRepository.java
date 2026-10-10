package com.example.chatLab.chatLab.repository;

import com.example.chatLab.chatLab.Entity.ChatParticipants;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatParticipantsRepository
        extends JpaRepository<ChatParticipants, Long> {

    boolean existsByChat_IdAndUser_Id(
            Long chatId,
            Long userId
    );

    List<ChatParticipants> findAllByUser_Id(Long userId);

    List<ChatParticipants> findAllByChat_Id(Long chatId);

    Optional<ChatParticipants> findByChat_IdAndUser_Id(
            Long chatId,
            Long userId
    );
}
