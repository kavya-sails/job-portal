package com.jobportal.user_service.controller;

import com.jobportal.user_service.entity.UserCredential;
import com.jobportal.user_service.repository.UserCredentialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class AuthIntrospectionController {
    private final UserCredentialRepository userCredentialRepository;

    @GetMapping("/introspect")
    public ResponseEntity<Map<String,Object>> introspect(@RequestHeader(value = "X-User-Id") String id) {
        Map<String,Object> resp = new HashMap<>();
        String error = "error";
        String active = "active";
        try {
            Long userId = Long.valueOf(id);
            boolean userOk = userCredentialRepository.findById(userId).map(UserCredential::getIsActive).orElse(false);
            if (!userOk) {
                resp.put(active, false);
                resp.put(error, "user_not_found_or_disabled");
            } else {
                resp.put(active, true);
            }
            return ResponseEntity.ok(resp);
        } catch (Exception ex) {
            resp.put(active, false);
            resp.put(error, ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
        }
    }
}