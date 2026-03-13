package com.baw.user_service.controller;

import com.baw.user_service.dto.UserDTO;
import com.baw.user_service.model.User;
import com.baw.user_service.request.CreateUserRequest;
import com.baw.user_service.request.RoleRequest;
import com.baw.user_service.request.UpdateUserRequest;
import com.baw.user_service.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final IUserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable UUID id) {
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping()
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
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
            @PathVariable UUID id) {

        UserDTO user = userService.updateUser(request, id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}/roles")
    public ResponseEntity<UserDTO> assignRoles(@PathVariable UUID id, @RequestBody RoleRequest request) {
        UserDTO user = userService.assignRole(id, request.getRole());
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(
            @PathVariable UUID id) {

        userService.deleteUser(id);
        return ResponseEntity.ok(Map.of("message", "User deactivated successfully"));
    }
}
