package com.ticketbooking.identity.user;

import com.ticketbooking.identity.security.GatewayHeaders;
import com.ticketbooking.identity.security.GatewayUser;
import com.ticketbooking.identity.user.dto.ChangePasswordRequest;
import com.ticketbooking.identity.user.dto.LogoutRequest;
import com.ticketbooking.identity.user.dto.UserResponse;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

            @AuthenticationPrincipal
            GatewayUser gatewayUser
    ) {

        UserResponse response = userService.getCurrentUser(gatewayUser.userId());

        return ResponseEntity.ok(response);
    }


    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal
            GatewayUser gatewayUser,

            @Valid
            @RequestBody
            ChangePasswordRequest request
    ) {

        userService.changePassword(gatewayUser.userId(), request);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @AuthenticationPrincipal
            GatewayUser gatewayUser,

            @Valid
            @RequestBody
            LogoutRequest request
    ) {

        userService.logout(gatewayUser.userId(),request.refreshToken());
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/logout-all")
    public ResponseEntity<Void> logoutAll(
            @AuthenticationPrincipal
            GatewayUser gatewayUser
    ) {

        userService.logoutAll(gatewayUser.userId());
        return ResponseEntity.noContent().build();
    }
}