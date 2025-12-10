package com.jobportal.user_service.service;


import com.jobportal.user_service.entity.AuthUser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;

    //  TOKEN GENERATION USING AuthUser
    public String generateToken(AuthUser user) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("username", user.getEmail());
        claims.put("role", user.getRole().getRoleName());
        claims.put("isActive", user.getIsActive());

        return Jwts
                .builder()
                .setClaims(claims)                                  // ⬅️ change is here
                .setSubject(String.valueOf(user.getUserId()))        // main identity
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSecretKey())
                .compact();
    }

    //  Secret key builder
    private SecretKey getSecretKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
