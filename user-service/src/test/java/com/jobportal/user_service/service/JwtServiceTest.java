package com.jobportal.user_service.service;

import com.jobportal.user_service.entity.Role;
import com.jobportal.user_service.entity.UserCredential;
import com.jobportal.user_service.enums.RoleName;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    @Test
    void generateToken_shouldGenerateValidJwtWithClaims() {
        JwtService jwtService = new JwtService();

        // HS256 needs 32+ byte secret
        String secret = "01234567890123456789012345678901";
        long expirationMs = 60_000L;

        ReflectionTestUtils.setField(jwtService, "secretKey", secret);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", expirationMs);

        Role role = new Role();
        role.setRoleName(RoleName.USER);

        UserCredential user = new UserCredential();
        user.setUserId(1L);
        user.setEmail("user@mail.com");
        user.setRole(role);
        user.setIsActive(true);

        String token = jwtService.generateToken(user);
        assertNotNull(token);
        assertFalse(token.isBlank());

        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertEquals("1", claims.getSubject());
        assertEquals("user@mail.com", claims.get("username", String.class));

        // role claim will be RoleName or String depending on how JWT lib serializes it,
        // safest is to compare as String
        assertEquals(RoleName.USER.name(), String.valueOf(claims.get("role")));

        assertEquals(Boolean.TRUE, claims.get("isActive", Boolean.class));
    }
}
