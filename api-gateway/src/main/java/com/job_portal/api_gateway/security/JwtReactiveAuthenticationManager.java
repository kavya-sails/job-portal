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

    //used only if introspection is enabled
    private final JwtIntrospectionClient introspectionClient;

    //if introspect.enabled:true then token will be verified by a remote server
    @Value("${auth.introspection.enabled:false}")
    private boolean introspectionEnabled;

    //url for introspect end point
    @Value("${auth.introspection.url:}")
    private String introspectionUrl;

    //returns a token wrapped inside a UsernamePasswordAuthenticationToken
    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {

        //Retrieve JWT string
        String token = (String) authentication.getCredentials();

        //Validate locally
        Jwt jwt;
        try {
            jwt = jwtTokenValidator.validateToken(token);
        } catch (Exception e) {
            log.warn("JWT local validation failed: {}", e.getMessage());
            return Mono.error(new BadCredentialsException("invalid_or_expired_token", e));
        }

        Long userId = jwtTokenValidator.getSubjectAsLong(jwt);
        String username = jwtTokenValidator.getUsername(jwt);
        String role = jwtTokenValidator.getRole(jwt);
        String jti = Optional.ofNullable(jwt.getId()).orElse("");

        if (!introspectionEnabled) {
            return Mono.just(buildSuccessAuth(userId, username, role, jti, jwt.getClaims()));
        }

        //if introspect enabled, calls the authorization server to verify if the token is active
        return introspectionClient.introspect(introspectionUrl, "Bearer " + token)
                .flatMap(map -> {
                    boolean active = Boolean.TRUE.equals(map.get("active"));

                    if (!active) {
                        log.warn("Introspection: token inactive");
                        return Mono.error(new BadCredentialsException("Introspection: token inactive"));
                    }
                    //if active extract remote claims
                    @SuppressWarnings("unchecked")
                    Map<String, Object> claims = (Map<String, Object>) map.get("claims");
                    return Mono.just(buildSuccessAuth(userId, username, role, jti, claims));
                })
                //if introspection unreachable fallback to local JWT validation
                .onErrorResume(ex -> {
                    log.error("Introspection call failed: {}", ex.getMessage());
                    return Mono.just(buildSuccessAuth(userId, username, role, jti, jwt.getClaims()));
                });
    }

    //creating authentication object to store in security context
    private Authentication buildSuccessAuth(Long userId, String username,
                                            String role, String jti,
                                            Map<String, Object> claims) {
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role.startsWith("ROLE_") ? role : "ROLE_" + role));
        var principal = new JwtAuthenticationPrincipal(userId, username, role, jti, claims);
        return new UsernamePasswordAuthenticationToken(principal, null, authorities);
    }

    public record JwtAuthenticationPrincipal(Long userId, String username, String role, String jti, Map<String, Object> claims) {
    }
}