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

    //Verifies the signature,parses the token,converts it into a jwt object
    private final JwtDecoder jwtDecoder;

    public Jwt validateToken(String token) {
        Jwt jwt = jwtDecoder.decode(token);

        //compare token expiry and not before values with this
        Instant now = Instant.now();
        if (jwt.getExpiresAt() != null && jwt.getExpiresAt().isBefore(now)) {
            throw new JwtException("token_expired");
        }else if (jwt.getNotBefore() != null && jwt.getNotBefore().isAfter(now)) {
            throw new JwtException("token_not_yet_valid");
        }
        return jwt;
    }

    //extract username from JWT claims
    public String getUsername(Jwt jwt) {
        return jwt.getClaims().get("username").toString();
    }

    //extract userId as long from the subject of the token
    public Long getSubjectAsLong(Jwt jwt) {
        String sub = jwt.getSubject();
        if (sub == null) return null;
        try { return Long.valueOf(sub); } catch (NumberFormatException e) { return null; }
    }
    //roles claim can be in different formats depending on your user service
    @SuppressWarnings("unchecked")
    public String getRole(Jwt jwt) {
        return jwt.getClaims().get("role").toString();
    }
}

