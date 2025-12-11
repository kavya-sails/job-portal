package com.jobportal.user_service.controller;

import com.jobportal.user_service.entity.UserCredential;
import com.jobportal.user_service.repository.UserCredentialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
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

    private final JwtDecoder jwtDecoder;
    private final UserCredentialRepository userCredentialRepository;

    @GetMapping("/introspect")
    public ResponseEntity<Map<String,Object>> introspect(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {
        Map<String,Object> resp = new HashMap<>();
        String error = "error";
        String active = "active";
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            resp.put(active, false);
            resp.put(error, "missing_token");
            return ResponseEntity.ok(resp);
        }
        String token = authorization.substring("Bearer ".length()).trim();
        try {
            Jwt jwt = jwtDecoder.decode(token);
            String sub = jwt.getSubject();
            Long userId = Long.valueOf(sub);
            boolean userOk = userCredentialRepository.findById(userId).map(UserCredential::getIsActive).orElse(false);
            if (!userOk) {
                resp.put(active, false);
                resp.put(error, "user_not_found_or_disabled");
            } else {
                Map<String, Object> claims = new HashMap<>(jwt.getClaims());
                resp.put(active, true);
                resp.put("claims", claims);
            }
            return ResponseEntity.ok(resp);
        } catch (Exception ex) {
            resp.put(active, false);
            resp.put(error, ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
        }
    }
}
