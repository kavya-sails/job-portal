package com.job_portal.api_gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class GatewayRoutesConfig {

    @Bean
    public RouteLocator customRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("user-route", r -> r
                        .path("/api/users/**")
                        .uri("lb://USER-SERVICE")
                )
                .route("job-route",r->r
                        .path("/api/jobs/**")
                        .uri("lb://JOB-SERVICE")
                )
                .build();
    }
}