package com.baw.user_service.repository;

import com.baw.user_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {

    Optional<User> findByEmailAndActiveTrue(String email);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
