package com.example.chatLab.chatLab.repository;

import com.example.chatLab.chatLab.Entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContactsRepository
        extends JpaRepository<Contact, Long> {

    boolean existsByOwner_IdAndContactUser_Id(
            Long ownerId,
            Long contactUserId
    );

    List<Contact> findAllByOwner_Id(Long ownerId);

    Optional<Contact> findByOwner_IdAndContactUser_Id(
            Long ownerId,
            Long contactUserId
    );
}