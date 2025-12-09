package com.job_portal.api_gateway.security;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

//makes a remote HTTP call to the introspection endpoint on your user-service.
@Component
@RequiredArgsConstructor
public class JwtIntrospectionClient {
    private final WebClient webClient;
    /**
     * Calls user-service introspect endpoint. Expected response shape:
     * { "active": true|false, "claims": {...} }
     */
    public Mono<Map> introspect(String url, String authHeader) {
        return webClient.get()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Map.class);
    }
}
