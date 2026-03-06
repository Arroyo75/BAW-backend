package com.baw.user_service.request;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
}
