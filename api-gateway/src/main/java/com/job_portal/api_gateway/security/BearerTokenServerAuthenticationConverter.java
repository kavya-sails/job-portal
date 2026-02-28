package com.job_portal.api_gateway.security;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

//convert HTTP requests into authentication objects.Defining how to extract authentication from webflux request
@Component
public class BearerTokenServerAuthenticationConverter implements ServerAuthenticationConverter {

    //Spring calls this method for every request
    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        List<String> headers = exchange.getRequest().getHeaders().getOrEmpty(HttpHeaders.AUTHORIZATION);

        //if no Authorization header return Mono.empty() means no authentication extracted
        if (headers.isEmpty()) return Mono.empty();

        //fetch the token starting with bearer
        String header = headers.getFirst();

        //if starts with bearer then valid or token format is invalid
        if (!header.startsWith("Bearer ")) return Mono.empty();

        //cut bearer from token string
        String token = header.substring(7);

        //Creates an authentication object
        return Mono.just(new UsernamePasswordAuthenticationToken(token, token));
    }
}