package com.example.chatLab.chatLab.repository;

import com.example.chatLab.chatLab.Entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessagesRepository extends JpaRepository<Message,Long> {
}
