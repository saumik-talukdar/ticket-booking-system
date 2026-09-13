package com.ticketbooking.identity.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(

        @NotBlank(
                message = "Reset token is required"
        )
        String resetToken,

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