package com.baw.user_service;

import com.baw.user_service.model.User;
import com.baw.user_service.service.TokenService;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import io.jsonwebtoken.JwtException;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class TokenServiceTest {

    private TokenService tokenService;

    @Mock
    private RedisTemplate<String, String> redis;

    @BeforeEach
    void setUp() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

        tokenService = new TokenService(privateKey, publicKey, redis);
        ReflectionTestUtils.setField(tokenService, "accessTokenExpiration", 900000L);
        ReflectionTestUtils.setField(tokenService, "refreshTokenExpiration", 604800000L);
    }

    @Test
    void shouldGenerateValidAccessToken() {
        User user = User.builder()
                .username("testuser")
                .email("test@baw.com")
                .passwordHash("irrelevant")
                .firstName("Test")
                .lastName("User")
                .phoneNumber("123456789")
                .build();

        ReflectionTestUtils.setField(user, "id", UUID.randomUUID());

        String token = tokenService.generateAccessToken(user);
        Claims claims = tokenService.validateToken(token);

        assertThat(claims.getSubject()).isEqualTo(user.getId().toString());
        assertThat(claims.get("X-User-Email")).isEqualTo("test@baw.com");
    }

    @Test
    void shouldRejectTamperedToken() {
        User user = User.builder()
                .username("testuser")
                .email("test@baw.com")
                .passwordHash("irrelevant")
                .firstName("Test")
                .lastName("User")
                .phoneNumber("123456789")
                .build();

        ReflectionTestUtils.setField(user, "id", UUID.randomUUID());

        String token = tokenService.generateAccessToken(user);
        String tampered = token.substring(0, token.length() - 5) + "XXXXX";

        assertThrows(JwtException.class, () -> tokenService.validateToken(tampered));
    }

}
