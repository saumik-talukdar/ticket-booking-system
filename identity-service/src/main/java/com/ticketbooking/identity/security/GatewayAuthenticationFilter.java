package com.ticketbooking.identity.security;

import com.ticketbooking.identity.user.Role;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Component;

import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class GatewayAuthenticationFilter
        extends OncePerRequestFilter {


    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String userIdHeader = request.getHeader(GatewayHeaders.USER_ID);

        String userRoleHeader = request.getHeader(GatewayHeaders.USER_ROLE);

        if (userIdHeader == null && userRoleHeader == null) {

            filterChain.doFilter(
                    request,
                    response
            );

            return;
        }

        if (userIdHeader == null || userRoleHeader == null) {

            log.warn("Incomplete gateway identity headers");

            SecurityContextHolder.clearContext();

            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Invalid authentication headers"
            );

            return;
        }


        try {

            UUID userId = UUID.fromString(userIdHeader);

            Role role = Role.valueOf(userRoleHeader);

            UsernamePasswordAuthenticationToken authentication =
                    getUsernamePasswordAuthenticationToken(userId, role);

            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);

        } catch (IllegalArgumentException exception) {

            log.warn("Invalid gateway authentication headers");

            SecurityContextHolder.clearContext();

            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Invalid authentication headers"
            );
        }
    }

    private static @NonNull UsernamePasswordAuthenticationToken
    getUsernamePasswordAuthenticationToken(UUID userId, Role role) {

        GatewayUser gatewayUser =
                new GatewayUser(
                        userId,
                        role
                );


        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role.name());

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        gatewayUser,
                        null,
                        List.of(authority)
                );
        return authentication;
    }
}