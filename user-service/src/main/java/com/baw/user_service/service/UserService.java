package com.baw.user_service.service;

import com.baw.user_service.dto.UserDTO;
import com.baw.user_service.exception.AlreadyExistsException;
import com.baw.user_service.exception.ResourceNotFoundException;
import com.baw.user_service.model.Role;
import com.baw.user_service.model.User;
import com.baw.user_service.repository.UserRepository;
import com.baw.user_service.request.CreateUserRequest;
import com.baw.user_service.request.UpdateUserRequest;
import com.baw.user_service.request.UserFilterRequest;
import com.baw.user_service.util.UserSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserService implements IUserService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDTO getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() ->  new ResourceNotFoundException("User not found"));
        return convertToDto(user);
    }

    @Override
    public Page<UserDTO> getUsers(UserFilterRequest filter) {
        Specification<User> spec = Specification
                .where(UserSpecification.matchesSearch(filter.getSearch()))
                .and(UserSpecification.isHasDogs(filter.getHasDogs()));

        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize(), Sort.by("createdAt").descending());
        return userRepository.findAll(spec, pageable)
                .map(this::convertToDto);
    }

    @Override
    public UserDTO createUser(CreateUserRequest request) {

        if(userRepository.existsByUsername(request.getUsername())) {
            throw new AlreadyExistsException("Username already exists: " + request.getUsername());
        }

        if(userRepository.existsByEmail(request.getEmail())) {
            throw new AlreadyExistsException("Email already exists: " + request.getEmail());
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
        log.info("User created successfully.");

        return convertToDto(savedUser);
    }

    @Override
    public UserDTO updateUser(UpdateUserRequest request, UUID id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (request.getUsername() != null && !request.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new AlreadyExistsException("Username taken");
            }
            user.setEmail(request.getUsername());
        }

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }

        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }

        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }

        User updatedUser = userRepository.save(user);
        log.info("User updated successfully");

        return convertToDto(updatedUser);
    }

    @Override
    public UserDTO assignRole(UUID id, Role role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.getRoles().add(role);
        User updatedUser = userRepository.save(user);
        return convertToDto(updatedUser);
    }

    @Override
    public void deactivateUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setActive(false);
        userRepository.save(user);
        log.info("User successfully deactivated.");
    }

    @Override
    public void purgeUser(UUID id) {
        if(!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found!");
        }
        userRepository.deleteById(id);
        log.info("User successfully purged");
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
                .hasDogs(user.getHasDogs())
                .roles(user.getRoles())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
