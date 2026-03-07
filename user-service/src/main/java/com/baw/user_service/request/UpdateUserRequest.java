package com.baw.user_service.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserRequest {

    @NotBlank
    @Size(min =3, max = 50)
    private String username;

    @Size(max = 30)
    private String firstName;

    @Size(max = 30)
    private String lastName;

    private String phoneNumber;
}
