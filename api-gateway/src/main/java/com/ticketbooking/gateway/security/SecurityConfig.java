package com.ticketbooking.gateway.security;

import com.ticketbooking.gateway.security.jwt.JwtValidator;

import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;

import org.springframework.security.config.web.server.SecurityWebFiltersOrder;

import org.springframework.security.config.web.server.ServerHttpSecurity;

import org.springframework.security.web.server.SecurityWebFilterChain;

import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;


@Configuration
@EnableWebFluxSecurity
@Slf4j
public class SecurityConfig {

    @Bean
    public JwtAuthenticationWebFilter jwtAuthenticationWebFilter(JwtValidator jwtValidator) {
        return new JwtAuthenticationWebFilter(jwtValidator);
    }


    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http,
            JwtAuthenticationWebFilter jwtAuthenticationWebFilter
    ) {

        log.info("Reactive Security configuration initialized");

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .logout(ServerHttpSecurity.LogoutSpec::disable)
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling
                                .authenticationEntryPoint(
                                        (exchange, exception) -> {
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
                                                      "message": "Authentication is required"
                                                    }
                                                    """;


                                            byte[] bytes = responseBody.getBytes(StandardCharsets.UTF_8);

                                            return exchange
                                                    .getResponse()
                                                    .writeWith(
                                                            Mono.just(
                                                                    exchange
                                                                            .getResponse()
                                                                            .bufferFactory()
                                                                            .wrap(bytes)
                                                            )
                                                    );
                                        }
                                )
                )

                .authorizeExchange(exchange ->
                        exchange
                                .pathMatchers(
                                        "/api/auth/**"
                                )
                                .permitAll()

                                .pathMatchers(
                                        "/actuator/health"
                                )
                                .permitAll()

                                .anyExchange()
                                .authenticated()
                )

                .addFilterAt(
                        jwtAuthenticationWebFilter,
                        SecurityWebFiltersOrder.AUTHENTICATION
                )

                .build();
    }
}