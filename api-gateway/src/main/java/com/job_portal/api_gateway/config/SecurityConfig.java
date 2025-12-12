package com.job_portal.api_gateway.config;

import com.job_portal.api_gateway.enums.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {
    private final ReactiveAuthenticationManager jwtReactiveAuthenticationManager;
    private final ServerAuthenticationConverter bearerTokenConverter;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        AuthenticationWebFilter authenticationWebFilter =
                new AuthenticationWebFilter(jwtReactiveAuthenticationManager);
        authenticationWebFilter.setServerAuthenticationConverter(bearerTokenConverter);
        // No session; store auth only in Reactor Context
        authenticationWebFilter.setSecurityContextRepository(NoOpServerSecurityContextRepository.getInstance());
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/api/users/login", "/api/users/register", "/actuator/**").permitAll()
                        .pathMatchers("/api/users", "/api/users/profile/all").hasRole(Role.ADMIN.name())
                        .pathMatchers(HttpMethod.DELETE,"/api/users/profile/{id}").hasRole(Role.ADMIN.name())
                        .pathMatchers(HttpMethod.PUT,"/api/users/profile/{id}").hasRole(Role.USER.name())
                        .pathMatchers(HttpMethod.PATCH,"/api/users/profile/{id}").hasRole(Role.USER.name())
                        .pathMatchers(HttpMethod.GET,"/api/users/profile//{id}" ).hasRole(Role.USER.name())
                        .pathMatchers(HttpMethod.POST,"/api/users/profile/create" ).hasRole(Role.USER.name())
                        .pathMatchers(HttpMethod.POST,"/api/jobs/applications/{jobId}").hasAnyRole(Role.USER.name())
                        .pathMatchers(HttpMethod.PUT,"/api/jobs/applications/{applicationId}/status").hasAnyRole(Role.RECRUITER.name())
                        .pathMatchers(HttpMethod.GET,"/api/jobs/applications/history/{userId}").hasAnyRole("ADMIN","USER")
                        .pathMatchers(HttpMethod.POST,"/api/jobs").hasAnyRole(Role.RECRUITER.name())
                        .pathMatchers(HttpMethod.PUT ,"/api/jobs/{jobId}").hasAnyRole(Role.RECRUITER.name())
                        .pathMatchers(HttpMethod.DELETE,"/api/jobs/{jobId}").hasAnyRole(Role.ADMIN.name())
                        .pathMatchers(HttpMethod.GET,"/api/jobs/**").permitAll()
                        .anyExchange().authenticated()
                )
                // Ensure our auth filter runs at AUTHENTICATION order (before AUTHORIZATION)
                .addFilterAt(authenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }
}