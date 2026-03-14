package com.baw.api_gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {

    private final ReactiveStringRedisTemplate redisTemplate;
    private final ReactiveJwtDecoder jwtDecoder;

    public JwtAuthFilter(ReactiveStringRedisTemplate redisTemplate, ReactiveJwtDecoder jwtDecoder) {
        this.redisTemplate = redisTemplate;
        this.jwtDecoder = jwtDecoder;
    }

    private static final List<String> PUBLIC_PATHS = List.of(
            "/actuator/health",
            "/api/auth/register",
            "/api/auth/login"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getPath().toString();

        if(PUBLIC_PATHS.stream().anyMatch(path::startsWith)) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);

        return jwtDecoder.decode(token)
                .flatMap(jwt -> {
                  String jti = jwt.getId();
                  return redisTemplate.hasKey("blacklist:" + jti)
                          .flatMap(blacklisted -> {
                              if(blacklisted) {
                                  exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                                  return exchange.getResponse().setComplete();
                              }
                              return chain.filter(exchange);
                          });
                })
                .onErrorResume(e -> {
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                });
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
