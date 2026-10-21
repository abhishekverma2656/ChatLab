
package com.example.chatLab.chatLab.repository;

import com.example.chatLab.chatLab.Entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MessagesRepository extends JpaRepository<Message, Long> {

    Page<Message> findAllByChat_Id(
            Long chatId,
            Pageable pageable
    );

    Optional<Message> findFirstByChat_IdOrderByCreatedAtDesc(
            Long chatId
    );
}
