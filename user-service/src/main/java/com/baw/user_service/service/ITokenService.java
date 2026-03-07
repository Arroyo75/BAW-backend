package com.baw.user_service.service;

import com.baw.user_service.model.User;
import io.jsonwebtoken.Claims;

public interface ITokenService {

    String generateAccessToken(User user);
    String generateRefreshToken(User user);
    Claims validateToken(String token);
}
