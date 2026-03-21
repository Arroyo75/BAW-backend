package com.baw.user_service.controller;

import com.baw.user_service.dto.AuthDTO;
import com.baw.user_service.dto.UserDTO;
import com.baw.user_service.request.CreateUserRequest;
import com.baw.user_service.request.LoginRequest;
import com.baw.user_service.request.RefreshRequest;
import com.baw.user_service.service.IAuthService;
import com.baw.user_service.util.CookieUtils;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final IAuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@Valid @RequestBody CreateUserRequest request) {
        UserDTO user = authService.register(request);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthDTO> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response) {
        AuthDTO result = authService.login(request);
        CookieUtils.addRefreshTokenCookie(response, result.getRefreshToken());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthDTO> refresh(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response) {

        if(refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        AuthDTO result = authService.refresh(refreshToken);
        CookieUtils.addRefreshTokenCookie(response, result.getRefreshToken());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader("Authorization") String authHeader,
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response) {

        if(refreshToken != null) {
            String accessToken = authHeader.replace("Bearer ", "");
            authService.logout(accessToken, refreshToken);
        }

        CookieUtils.clearRefreshTokenCookie(response);
        return ResponseEntity.noContent().build();
    }
}
