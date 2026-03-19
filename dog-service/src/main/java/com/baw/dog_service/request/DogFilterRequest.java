package com.baw.dog_service.request;

import lombok.Data;

@Data
public class DogFilterRequest {
    private String search;
    private int page = 0;
    private int size = 20;
}
