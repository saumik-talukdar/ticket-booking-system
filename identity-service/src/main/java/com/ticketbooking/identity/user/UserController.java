package com.ticketbooking.identity.user;

import com.ticketbooking.identity.security.GatewayHeaders;
import com.ticketbooking.identity.user.dto.ChangePasswordRequest;
import com.ticketbooking.identity.user.dto.LogoutRequest;
import com.ticketbooking.identity.user.dto.UserResponse;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.GetMapping;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(

            @RequestHeader(GatewayHeaders.USER_ID)
            UUID userId
    ) {

        UserResponse response = userService.getCurrentUser(userId);

        return ResponseEntity.ok(response);
    }


    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(
            @RequestHeader(GatewayHeaders.USER_ID)
            UUID userId,

            @Valid
            @RequestBody
            ChangePasswordRequest request
    ) {

        userService.changePassword(userId, request);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @Valid
            @RequestBody
            LogoutRequest request
    ) {

        userService.logout(request.refreshToken());
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/logout-all")
    public ResponseEntity<Void> logoutAll(
            @RequestHeader(GatewayHeaders.USER_ID)
            UUID userId
    ) {

        userService.logoutAll(userId);
        return ResponseEntity.noContent().build();
    }
}