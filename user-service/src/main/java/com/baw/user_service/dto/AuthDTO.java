package com.baw.user_service.dto;

import com.baw.user_service.model.Role;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Builder
@Data
public class AuthDTO {
    String accessToken;
    String refreshToken;
    String tokenType;
    long expiresIn;
    UUID userId;
    Role role;
}
