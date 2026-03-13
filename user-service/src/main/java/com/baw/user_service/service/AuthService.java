package com.baw.user_service.service;

import com.baw.user_service.dto.AuthDTO;
import com.baw.user_service.dto.UserDTO;
import com.baw.user_service.exception.DataIntegrityViolationException;
import com.baw.user_service.exception.InvalidTokenException;
import com.baw.user_service.model.RefreshToken;
import com.baw.user_service.model.Role;
import com.baw.user_service.model.User;
import com.baw.user_service.repository.TokenRepository;
import com.baw.user_service.repository.UserRepository;
import com.baw.user_service.request.CreateUserRequest;
import com.baw.user_service.request.LoginRequest;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional
public class AuthService implements IAuthService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    @Value(("${jwt.refresh-expiration}"))
    private long refreshTokenExpiration;

    @Override
    public UserDTO register(CreateUserRequest request) {
        if(userRepository.existsByEmail(request.getEmail())) {
            throw new DataIntegrityViolationException("Registration failed");
        }

        User user = User.builder()
                .username(request.getUsername())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .build();

        User savedUser = userRepository.save(user);
        return convertToDto(savedUser);
    }

    @Override
    public AuthDTO login(LoginRequest request) {
        User user = userRepository.findByEmailAndActiveTrue(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials."));

        if(!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid credentials.");
        }

        String accessToken = tokenService.generateAccessToken(user);
        String refreshToken = createRefreshToken(user);

        return AuthDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(refreshTokenExpiration)
                .userId(user.getId())
                .roles(user.getRoles())
                .build();
    }

    @Override
    public AuthDTO refresh(String refreshTokenValue) {
        RefreshToken stored = tokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new InvalidTokenException("Invalid refresh token"));

        //token already used or revoked
        if (stored.isRevoked()) {
            //revoke the entire family — potential theft
            tokenRepository.revokeFamily(stored.getFamily());
            throw new InvalidTokenException("Refresh token reuse detected");
        }

        if (stored.getExpiresAt().isBefore(Instant.now())) {
            throw new InvalidTokenException("Refresh token expired");
        }

        stored.setRevoked(true);
        tokenRepository.save(stored);

        User user = stored.getUser();
        String newAccessToken = tokenService.generateAccessToken(user);
        String newRefreshToken = createRefreshToken(user, stored.getFamily()); // same family

        return AuthDTO.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(refreshTokenExpiration)
                .userId(user.getId())
                .roles(user.getRoles())
                .build();
    }

    @Override
    public void logout(String accessToken, String refreshTokenValue) {
        Claims claims = tokenService.validateToken(accessToken);
        long remaining = claims.getExpiration().getTime() - System.currentTimeMillis();
        tokenService.blacklist(claims.getId(), remaining);

        tokenRepository.findByToken(refreshTokenValue)
                .ifPresent(rt -> {
                    rt.setRevoked(true);
                    tokenRepository.save(rt);
                });
    }

    private String createRefreshToken(User user) {
        return createRefreshToken(user, UUID.randomUUID().toString());
    }

    private String createRefreshToken(User user, String family) {
        String token = tokenService.generateRefreshToken(user);

        RefreshToken entity = new RefreshToken();
        entity.setToken(token);
        entity.setUser(user);
        entity.setFamily(family);
        entity.setExpiresAt(Instant.now().plusMillis(refreshTokenExpiration));

        tokenRepository.save(entity);
        return token;
    }

    private UserDTO convertToDto(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .active(user.getActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
