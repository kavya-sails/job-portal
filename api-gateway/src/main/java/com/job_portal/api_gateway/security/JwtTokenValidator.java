package com.job_portal.api_gateway.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.util.*;

@Component
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JwtTokenValidator {

    private final JwtDecoder jwtDecoder;

    public Jwt validateToken(String token) {
        Jwt jwt = jwtDecoder.decode(token);
        Instant now = Instant.now();
        if (jwt.getExpiresAt() != null && jwt.getExpiresAt().isBefore(now)) {
            throw new JwtException("token_expired");
        }else if (jwt.getNotBefore() != null && jwt.getNotBefore().isAfter(now)) {
            throw new JwtException("token_not_yet_valid");
        }
        return jwt;
    }

    public String getUsername(Jwt jwt) {
        return jwt.getClaims().get("username").toString();
    }

    public Long getSubjectAsLong(Jwt jwt) {
        String sub = jwt.getSubject();
        if (sub == null) return null;
        try { return Long.valueOf(sub); } catch (NumberFormatException e) { return null; }
    }

    @SuppressWarnings("unchecked")
    public List<String> getRoles(Jwt jwt) {
        Object roles = jwt.getClaims().get("roles");
        return switch (roles) {
            case null -> Collections.emptyList();
            case List r -> (List<String>) roles;
            case String s -> List.of(s.split(","));
            default -> List.of(String.valueOf(roles));
        };
    }
}

