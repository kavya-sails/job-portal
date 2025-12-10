package com.jobportal.user_service.dto;

import lombok.Data;

@Data
public class RegisterRequest {

    private String email;
    private String password;
    private String roleName;   // ADMIN or USER
}
