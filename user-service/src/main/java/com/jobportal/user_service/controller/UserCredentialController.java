package com.jobportal.user_service.controller;

import com.jobportal.user_service.dto.LoginRequest;
import com.jobportal.user_service.dto.RegisterRequest;
import com.jobportal.user_service.entity.UserCredential;
import com.jobportal.user_service.service.UserCredentialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor//(onConstructor_ = @Autowired)
public class UserCredentialController {

    private final UserCredentialService userCredentialService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userCredentialService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<String>  login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(userCredentialService.login(request));
    }

    @GetMapping
    public List<UserCredential> getAllUsers() {
        return userCredentialService.findAll();
    }
}
