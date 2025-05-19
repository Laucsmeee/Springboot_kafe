package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<com.example.demo.entity.User, Long> {
    Optional<com.example.demo.entity.User> findByUsername(String username);
}