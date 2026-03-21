package com.baw.user_service.dto;

import com.baw.user_service.model.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Builder
@Data
public class AuthDTO {
    String accessToken;

    @JsonIgnore
    String refreshToken; //never use without JsonIgnore

    String tokenType;
    long expiresIn;
    UUID userId;
    Set<Role> roles;
}
