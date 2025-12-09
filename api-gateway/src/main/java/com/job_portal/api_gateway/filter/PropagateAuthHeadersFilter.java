package com.job_portal.api_gateway.filter;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import com.job_portal.api_gateway.security.JwtReactiveAuthenticationManager;

@Component
public class PropagateAuthHeadersFilter implements WebFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        //Retrieves Spring Security's reactive context,contains the Authentication object if the user is authenticated
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .filter(auth -> auth != null && auth.isAuthenticated())
                .flatMap(auth -> {
                    if (auth == null || !(auth.getPrincipal() instanceof JwtReactiveAuthenticationManager.JwtAuthenticationPrincipal p)) {
                        return chain.filter(exchange);
                    }

                    //builds a new mutated request
                    var mutated = exchange.getRequest().mutate()
                            .header("X-User-Id", String.valueOf(p.userId()))
                            .header("X-User-Username", p.username())
                            .build();

                    //returns new request containing added headers
                    return chain.filter(exchange.mutate().request(mutated).build());
                })
                .switchIfEmpty(chain.filter(exchange));
    }
}