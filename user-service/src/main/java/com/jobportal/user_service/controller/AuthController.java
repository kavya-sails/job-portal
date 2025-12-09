package com.jobportal.user_service.controller;

import com.jobportal.user_service.dto.LoginRequest;
import com.jobportal.user_service.dto.RegisterRequest;
import com.jobportal.user_service.service.AuthUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthUserService authUserService;

    //  REGISTER API
    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {
        return authUserService.register(request);
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {
        return authUserService.login(request);
    }

}
