package com.baw.user_service.repository;

import com.baw.user_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmailAndActiveTrue(String email);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
