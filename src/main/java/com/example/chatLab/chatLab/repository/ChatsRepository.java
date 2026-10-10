package com.example.chatLab.chatLab.repository;

import com.example.chatLab.chatLab.Entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatsRepository extends JpaRepository<Chat, Long> {

    Optional<Chat> findByDirectKey(String directKey);
}
