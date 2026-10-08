package com.example.chatLab.chatLab.repository;

import com.example.chatLab.chatLab.Entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactsRepository extends JpaRepository<Contact,Long> {
}
