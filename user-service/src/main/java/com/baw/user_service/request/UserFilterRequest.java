package com.baw.user_service.request;

import lombok.Data;

@Data
public class UserFilterRequest {
    private String search;
    private Boolean hasDogs;
    private int page = 0;
    private int size = 20;
}
