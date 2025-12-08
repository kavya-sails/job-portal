package com.job_portal.api_gateway.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtReactiveAuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtTokenValidator jwtTokenValidator;
    private final JwtIntrospectionClient introspectionClient;

    @Value("${auth.introspection.enabled:false}")
    private boolean introspectionEnabled;
    @Value("${auth.introspection.url:}")
    private String introspectionUrl;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String token = (String) authentication.getCredentials();

        Jwt jwt;
        try {
            jwt = jwtTokenValidator.validateToken(token);
        } catch (Exception e) {
            log.warn("JWT local validation failed: {}", e.getMessage());
            return Mono.error(new BadCredentialsException("invalid_or_expired_token", e));
        }

        Long userId = jwtTokenValidator.getSubjectAsLong(jwt);
        String username = jwtTokenValidator.getUsername(jwt);
        List<String> roles = jwtTokenValidator.getRoles(jwt);
        String jti = Optional.ofNullable(jwt.getId()).orElse("");

        if (!introspectionEnabled) {
            return Mono.just(buildSuccessAuth(userId, username, roles, jti, jwt.getClaims()));
        }

        return introspectionClient.introspect(introspectionUrl, "Bearer " + token)
                .flatMap(map -> {
                    boolean active = Boolean.TRUE.equals(map.get("active"));
                    if (!active) {
                        log.warn("Introspection: token inactive");
                        return Mono.error(new BadCredentialsException("Introspection: token inactive"));
                    }
                    @SuppressWarnings("unchecked")
                    Map<String, Object> claims = (Map<String, Object>) map.get("claims");
                    return Mono.just(buildSuccessAuth(userId, username, roles, jti, claims));
                })
                .onErrorResume(ex -> {
                    log.error("Introspection call failed: {}", ex.getMessage());
                    return Mono.just(buildSuccessAuth(userId, username, roles, jti, jwt.getClaims()));
                });
    }

    private Authentication buildSuccessAuth(Long userId, String username,
                                            List<String> roles, String jti,
                                            Map<String, Object> claims) {
        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r)
                .map(SimpleGrantedAuthority::new)
                .toList();
        var principal = new JwtAuthenticationPrincipal(userId, username, jti, claims);
        return new UsernamePasswordAuthenticationToken(principal, null, authorities);
    }

    public record JwtAuthenticationPrincipal(Long userId, String username, String jti, Map<String, Object> claims) {
    }
}