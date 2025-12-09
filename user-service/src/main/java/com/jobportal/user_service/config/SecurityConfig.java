package com.jobportal.user_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                //  Disable CSRF because we use REST APIs
                .csrf(AbstractHttpConfigurer::disable)

                //  No session will be created or used
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

//                //  Allow login API without authentication
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/api/users/login").permitAll()
//                        .anyRequest().authenticated()
//                )

                // URL authorization rules
                .authorizeHttpRequests(auth -> auth
                        // all public endpoints first

                        .requestMatchers("/test/**").permitAll()

                        // 2) Login is public too (if you want)
                        .requestMatchers("/api/users/login").permitAll()

                        .requestMatchers("/api/auth/**").permitAll()


                        // everything else must be authenticated
                        .anyRequest().authenticated()
                )

                //  Enable basic authentication for testing (Postman)
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
