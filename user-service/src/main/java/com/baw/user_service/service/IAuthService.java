package com.baw.user_service.service;

import com.baw.user_service.dto.AuthDTO;
import com.baw.user_service.dto.UserDTO;
import com.baw.user_service.request.CreateUserRequest;
import com.baw.user_service.request.LoginRequest;

public interface IAuthService {

    UserDTO register(CreateUserRequest request);
    AuthDTO login(LoginRequest request);
    AuthDTO refresh(String refreshTokenValue);
    void logout(String accessToken, String refreshTokenValue);

}
