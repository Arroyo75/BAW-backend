package com.baw.user_service.service;

import com.baw.user_service.model.Role;
import com.baw.user_service.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TokenService implements ITokenService {

    private final RSAPrivateKey privateKey;
    private final RSAPublicKey publicKey;
    private final RedisTemplate<String, String> redis;

    @Value("${jwt.expiration}")
    private long accessTokenExpiration;

    @Value(("${jwt.refresh-expiration}"))
    private long refreshTokenExpiration;

    @Override
    public String generateAccessToken(User user) {
        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("X-User-Roles", user.getRoles().stream()
                        .map(Role::name)
                        .collect(Collectors.toList()))
                .claim("X-User-Email", user.getEmail())
                .id(UUID.randomUUID().toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(privateKey)
                .compact();
    }

    @Override
    public String generateRefreshToken(User user) {
        return Jwts.builder()
                .subject(user.getId().toString())
                .id(UUID.randomUUID().toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .signWith(privateKey)
                .compact();
    }

    @Override
    public Claims validateToken(String token) {
        return Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isBlacklisted(String jti) {
        return Boolean.TRUE.equals(redis.hasKey("blacklist:" + jti));
    }

    public void blacklist(String jti, long ttlMs) {
        redis.opsForValue().set(
                "blacklist:" + jti,
                "revoked",
                ttlMs,
                TimeUnit.MILLISECONDS
        );
    }
}
