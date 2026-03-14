package com.baw.user_service.service;

import com.baw.user_service.dto.UserDTO;
import com.baw.user_service.model.Role;
import com.baw.user_service.request.CreateUserRequest;
import com.baw.user_service.request.UpdateUserRequest;

import java.util.List;
import java.util.UUID;

public interface IUserService {

    UserDTO getUserById(UUID id);

    List<UserDTO> getAllUsers();

    UserDTO createUser(CreateUserRequest request);

    UserDTO updateUser(UpdateUserRequest request, UUID id);
    UserDTO assignRole(UUID id, Role role);

    void deactivateUser(UUID id);
    void purgeUser(UUID id);

}
