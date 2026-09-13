package com.ticketbooking.identity.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(

        @NotBlank(
                message = "Current password is required"
        )
        String currentPassword,

        @NotBlank(
                message = "New password is required"
        )
        @Size(
                min = 8,
                max = 100,
                message = "Password must be between 8 and 100 characters"
        )
        String newPassword

) {
}