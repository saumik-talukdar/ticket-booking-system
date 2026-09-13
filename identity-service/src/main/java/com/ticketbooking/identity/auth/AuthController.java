package com.ticketbooking.identity.auth;

import com.ticketbooking.identity.auth.dto.AuthResponse;
import com.ticketbooking.identity.auth.dto.ForgotPasswordRequest;
import com.ticketbooking.identity.auth.dto.LoginRequest;
import com.ticketbooking.identity.auth.dto.RefreshTokenRequest;
import com.ticketbooking.identity.auth.dto.RegisterRequest;
import com.ticketbooking.identity.auth.dto.ResetPasswordRequest;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;



    @PostMapping("/register")
    public ResponseEntity<Void> register(
            @Valid @RequestBody RegisterRequest request
    ) {

        authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }


    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {

        AuthResponse response =
                authService.login(request);

        return ResponseEntity.ok(response);
    }


    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @Valid @RequestBody RefreshTokenRequest request
    ) {

        AuthResponse response =
                authService.refresh(request);

        return ResponseEntity.ok(response);
    }



    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request
    ) {

        authService.forgotPassword(request);

        return ResponseEntity
                .status(HttpStatus.NOT_IMPLEMENTED)
                .build();
    }


    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request
    ) {

        authService.resetPassword(request);

        return ResponseEntity
                .status(HttpStatus.NOT_IMPLEMENTED)
                .build();
    }

}
