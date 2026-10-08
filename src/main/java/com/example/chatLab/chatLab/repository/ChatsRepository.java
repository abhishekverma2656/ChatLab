package com.example.chatLab.chatLab.repository;

import com.example.chatLab.chatLab.Entity.Chats;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatsRepository extends JpaRepository<Chats,Long> {
}
