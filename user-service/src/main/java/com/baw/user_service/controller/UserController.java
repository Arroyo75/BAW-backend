package com.baw.user_service.controller;

import com.baw.user_service.dto.UserDTO;
import com.baw.user_service.model.User;
import com.baw.user_service.request.CreateUserRequest;
import com.baw.user_service.request.RoleRequest;
import com.baw.user_service.request.UpdateUserRequest;
import com.baw.user_service.request.UserFilterRequest;
import com.baw.user_service.service.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final IUserService userService;

    @PreAuthorize("#id.toString() == authentication.name || hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable UUID id) {
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping()
    public ResponseEntity<Page<UserDTO>> getUsers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean hasDogs,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {

        UserFilterRequest filter = new UserFilterRequest();
        filter.setSearch(search);
        filter.setHasDogs(hasDogs);
        filter.setPage(page);
        filter.setSize(size);

        Page<UserDTO> users = userService.getUsers(filter);
        return ResponseEntity.ok(users);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping()
    public ResponseEntity<UserDTO> createUser(
            @Valid @RequestBody CreateUserRequest request) {

        UserDTO user = userService.createUser(request);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PreAuthorize("#id.toString() == authentication.name || hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(
            @Valid @RequestBody UpdateUserRequest request,
            @PathVariable UUID id) {

        UserDTO user = userService.updateUser(request, id);
        return ResponseEntity.ok(user);
    }

    //@PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/roles")
    public ResponseEntity<UserDTO> assignRoles(@PathVariable UUID id, @RequestBody RoleRequest request) {
        UserDTO user = userService.assignRole(id, request.getRole());
        return ResponseEntity.ok(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable UUID id) {
        userService.deactivateUser(id);
        return ResponseEntity.ok(Map.of("message", "User deactivated successfully"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}/purge")
    public ResponseEntity<Map<String, String>> purgeUser(@PathVariable UUID id) {
        userService.purgeUser(id);
        return ResponseEntity.ok(Map.of("message", "User purged successfully"));
    }
}
