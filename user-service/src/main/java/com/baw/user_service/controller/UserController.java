package com.baw.user_service.controller;

import com.baw.user_service.dto.UserDTO;
import com.baw.user_service.request.CreateUserRequest;
import com.baw.user_service.request.UpdateUserRequest;
import com.baw.user_service.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final IUserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping()
    public ResponseEntity<List<UserDTO>> getAllUsers() {       List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PostMapping()
    public ResponseEntity<UserDTO> createUser(
            @RequestBody CreateUserRequest request) {

        UserDTO user = userService.createUser(request);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(
            @RequestBody UpdateUserRequest request,
            @PathVariable Long id) {

        UserDTO user = userService.updateUser(request, id);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(
            @PathVariable Long id) {

        userService.deleteUser(id);
        return ResponseEntity.ok(Map.of("message", "User deactivated successfully"));
    }
}
