package com.example.chatLab.chatLab.repository;

import com.example.chatLab.chatLab.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
}
