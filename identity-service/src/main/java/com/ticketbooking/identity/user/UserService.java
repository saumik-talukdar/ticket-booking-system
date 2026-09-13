package com.ticketbooking.identity.user;

import com.ticketbooking.identity.exception.InvalidCredentialsException;
import com.ticketbooking.identity.exception.UserNotFoundException;
import com.ticketbooking.identity.token.RefreshTokenService;
import com.ticketbooking.identity.user.dto.ChangePasswordRequest;
import com.ticketbooking.identity.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final RefreshTokenService refreshTokenService;

    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(UUID userId) {

        User user = getUserOrThrow(userId);

        return mapToUserResponse(user);
    }

    @Transactional
    public void changePassword(UUID userId, ChangePasswordRequest request
    ) {

        User user = getUserOrThrow(userId);

        validateUserIsActive(user);

        if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {

            throw new InvalidCredentialsException("Invalid Credentials.");
        }

        validateNewPassword(request, user);


        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));

        refreshTokenService.revokeAllRefreshTokens(userId);
    }


    public void logout(String refreshToken) {

        refreshTokenService.revokeRefreshToken(refreshToken);
    }


    public void logoutAll(UUID userId) {

        refreshTokenService.revokeAllRefreshTokens(userId);
    }






    private User getUserOrThrow(UUID userId) {

        return userRepository
                .findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }


    private void validateUserIsActive(User user) {

        if (user.getStatus() != UserStatus.ACTIVE) {

            throw new IllegalStateException("User account is not active");
        }
    }


    private void validateNewPassword(ChangePasswordRequest request, User user) {

        if (passwordEncoder.matches(request.newPassword(), user.getPasswordHash())) {

            throw new IllegalArgumentException("New password must be different from current password");
        }
    }


    private UserResponse mapToUserResponse(User user) {

        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole().toString(),
                user.getStatus().toString(),
                user.getCreatedAt()
        );
    }
}