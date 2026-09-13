package com.ticketbooking.gateway.security;

import com.ticketbooking.gateway.common.GatewayHeaders;
import com.ticketbooking.gateway.security.jwt.InvalidJwtException;
import com.ticketbooking.gateway.security.jwt.JwtUser;
import com.ticketbooking.gateway.security.jwt.JwtValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;

import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;


@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationWebFilter implements WebFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtValidator jwtValidator;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        ServerWebExchange sanitizedExchange = sanitizeIncomingRequest(exchange);


        String authorizationHeader =
                sanitizedExchange
                        .getRequest()
                        .getHeaders()
                        .getFirst(HttpHeaders.AUTHORIZATION);


        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            return chain.filter(sanitizedExchange);
        }

        if (!authorizationHeader.startsWith(BEARER_PREFIX)) {

            return unauthorized(sanitizedExchange, "Invalid Authorization header");
        }


        String token = authorizationHeader.substring(BEARER_PREFIX.length());

        if (token.isBlank()) {
            return unauthorized(sanitizedExchange, "JWT token is missing");
        }


        try {
            JwtUser jwtUser = jwtValidator.validateAndExtract(token);
            ServerWebExchange authenticatedExchange =
                    createAuthenticatedExchange(
                            sanitizedExchange,
                            jwtUser
                    );

            UsernamePasswordAuthenticationToken authentication = createAuthentication(jwtUser);

            return chain
                    .filter(authenticatedExchange)
                    .contextWrite(
                            ReactiveSecurityContextHolder.withAuthentication(authentication)
                    );


        } catch (InvalidJwtException exception) {

            log.warn(
                    "JWT validation failed: {} {}",
                    sanitizedExchange.getRequest().getMethod(),
                    sanitizedExchange.getRequest().getPath()
            );

            return unauthorized(
                    sanitizedExchange,
                    "Invalid or expired JWT token"
            );

        } catch (Exception exception) {

            log.error(
                    "Unexpected JWT authentication error",
                    exception
            );

            return unauthorized(
                    sanitizedExchange,
                    "Authentication failed"
            );
        }
    }

    private ServerWebExchange sanitizeIncomingRequest(ServerWebExchange exchange) {

        String requestId = UUID.randomUUID().toString();


        ServerHttpRequest sanitizedRequest =
                exchange
                        .getRequest()
                        .mutate()
                        .headers(headers -> {
                            GatewayHeaders.RESERVED_HEADERS.forEach(headers::remove);

                            headers.set(GatewayHeaders.REQUEST_ID, requestId);

                        })
                        .build();


        return exchange
                .mutate()
                .request(sanitizedRequest)
                .build();
    }

    private ServerWebExchange createAuthenticatedExchange(ServerWebExchange exchange, JwtUser jwtUser) {

        ServerHttpRequest authenticatedRequest =
                exchange
                        .getRequest()
                        .mutate()
                        .headers(headers -> {

                            headers.remove(HttpHeaders.AUTHORIZATION);

                            headers.remove(GatewayHeaders.USER_ID);

                            headers.remove(GatewayHeaders.USER_ROLE);

                            headers.set(GatewayHeaders.USER_ID, jwtUser.userId().toString());

                            headers.set(GatewayHeaders.USER_ROLE, jwtUser.role());

                        })
                        .build();


        return exchange
                .mutate()
                .request(authenticatedRequest)
                .build();
    }

    private UsernamePasswordAuthenticationToken createAuthentication(JwtUser jwtUser) {

        String normalizedRole = jwtUser.role().trim().toUpperCase();

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + normalizedRole);

        return new UsernamePasswordAuthenticationToken(
                jwtUser,
                null,
                List.of(authority)
        );
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {

        exchange
                .getResponse()
                .setStatusCode(HttpStatus.UNAUTHORIZED);


        exchange
                .getResponse()
                .getHeaders()
                .setContentType(MediaType.APPLICATION_JSON);


        String responseBody =
                """
                {
                  "status": 401,
                  "error": "Unauthorized",
                  "message": "%s"
                }
                """.formatted(message);


        byte[] bytes = responseBody.getBytes(StandardCharsets.UTF_8);

        DataBuffer buffer =
                exchange
                        .getResponse()
                        .bufferFactory()
                        .wrap(bytes);

        return exchange
                .getResponse()
                .writeWith(Mono.just(buffer));
    }
}