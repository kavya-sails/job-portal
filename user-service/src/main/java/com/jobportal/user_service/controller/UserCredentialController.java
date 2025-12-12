package com.jobportal.user_service.controller;

import com.jobportal.user_service.dto.LoginRequest;
import com.jobportal.user_service.dto.RegisterRequest;
import com.jobportal.user_service.entity.UserCredential;
import com.jobportal.user_service.service.UserCredentialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(
        name = "User Authentication",
        description = "Handles user registration, login, and credential management."
)
public class UserCredentialController {
    private final UserCredentialService userCredentialService;

    @Operation(
            summary = "Register a new user",
            description = "Creates a new user using email and password, saves encrypted password."
    )
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userCredentialService.register(request));
    }

    @Operation(
            summary = "User login",
            description = "Authenticates user and returns a response (JWT token if configured)."
    )
    @PostMapping("/login")
    public ResponseEntity<String>  login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(userCredentialService.login(request));
    }

    @Operation(
            summary = "Get all user credentials",
            description = "Fetches list of all users (internal use only)."
    )
    @GetMapping
    public List<UserCredential> getAllUsers() {
        return userCredentialService.findAll();
    }
}
