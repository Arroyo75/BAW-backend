package com.baw.user_service.controller;

import com.baw.user_service.dto.AuthDTO;
import com.baw.user_service.dto.UserDTO;
import com.baw.user_service.request.CreateUserRequest;
import com.baw.user_service.request.LoginRequest;
import com.baw.user_service.request.RefreshRequest;
import com.baw.user_service.service.IAuthService;
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
    public ResponseEntity<AuthDTO> login(@Valid @RequestBody LoginRequest request) {
        AuthDTO response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthDTO> refresh(@Valid @RequestBody RefreshRequest request) {
        AuthDTO response = authService.refresh(request.getRefreshToken());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody RefreshRequest request) {
        String accessToken = authHeader.replace("Bearer ", "");
        authService.logout(accessToken, request.getRefreshToken());
        return ResponseEntity.noContent().build();
    }
}
