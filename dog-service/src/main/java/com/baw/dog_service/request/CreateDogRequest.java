package com.baw.dog_service.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateDogRequest {

    @NotBlank
    @Size(min = 1, max = 50)
    private String nickname;

    @NotBlank
    @Size(min = 1, max = 50)
    private String breed;

    @Min(0)
    @Max(30)
    private Integer age;

    private String image;

    @NotBlank
    @Size(max = 1000)
    private String description;
}
