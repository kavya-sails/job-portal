package com.jobportal.user_service.service;

import com.jobportal.user_service.entity.AuthUser;
import com.jobportal.user_service.entity.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService,
                "secretKey",
                "test-secret-key-test-secret-key-test-secret-key");
        ReflectionTestUtils.setField(jwtService,
                "jwtExpiration",
                3600000L); // 1 hour
    }

    @Test
    void generateToken_shouldContainClaimsAndSubject() {
        AuthUser user = AuthUser.builder()
                .userId(42L)
                .email("user@example.com")
                .role(Role.builder().roleName("USER").build())
                .isActive(true)
                .build();

        String token = jwtService.generateToken(user);

        byte[] keyBytes =
                "test-secret-key-test-secret-key-test-secret-key".getBytes(StandardCharsets.UTF_8);

        Jws<Claims> jws = Jwts.parserBuilder()
                .setSigningKey(keyBytes)
                .build()
                .parseClaimsJws(token);

        Claims claims = jws.getBody();
        assertThat(claims.getSubject()).isEqualTo("42");
        assertThat(claims.get("username", String.class)).isEqualTo("user@example.com");
        assertThat(claims.get("role", String.class)).isEqualTo("USER");
        assertThat(claims.get("isActive", Boolean.class)).isTrue();

        Date expiration = claims.getExpiration();
        assertThat(expiration).isAfter(new Date());
    }
}
