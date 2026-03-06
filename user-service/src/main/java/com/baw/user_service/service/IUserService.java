package com.baw.user_service.service;

import com.baw.user_service.dto.UserDTO;
import com.baw.user_service.request.CreateUserRequest;
import com.baw.user_service.request.UpdateUserRequest;

import java.util.List;

public interface IUserService {

    UserDTO getUserById(Long id);

    List<UserDTO> getAllUsers();

    UserDTO createUser(CreateUserRequest request);
    UserDTO updateUser(UpdateUserRequest request, Long id);

    void deleteUser(Long id);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
