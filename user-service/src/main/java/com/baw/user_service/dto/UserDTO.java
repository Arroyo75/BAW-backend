package com.baw.user_service.dto;

import com.baw.user_service.model.Role;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Builder
@Data
public class UserDTO {
    private UUID id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private Boolean active;
    private Boolean hasDogs;
    private Set<Role> roles;
    private Instant createdAt;
    private Instant updatedAt;
}
