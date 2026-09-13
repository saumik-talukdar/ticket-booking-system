package com.ticketbooking.identity.auth;

import com.ticketbooking.identity.auth.dto.AuthResponse;
import com.ticketbooking.identity.auth.dto.ForgotPasswordRequest;
import com.ticketbooking.identity.auth.dto.LoginRequest;
import com.ticketbooking.identity.auth.dto.RefreshTokenRequest;
import com.ticketbooking.identity.auth.dto.RegisterRequest;
import com.ticketbooking.identity.auth.dto.ResetPasswordRequest;
import com.ticketbooking.identity.exception.InvalidCredentialsException;
import com.ticketbooking.identity.exception.UserAlreadyExistsException;
import com.ticketbooking.identity.security.jwt.JwtService;
import com.ticketbooking.identity.token.RefreshTokenRotationResult;
import com.ticketbooking.identity.token.RefreshTokenService;
import com.ticketbooking.identity.user.Role;
import com.ticketbooking.identity.user.User;
import com.ticketbooking.identity.user.UserRepository;
import com.ticketbooking.identity.user.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final RefreshTokenService refreshTokenService;


    @Transactional
    public void register(RegisterRequest request) {

        String email = normalizeEmail(request.email());

        if (userRepository.existsByEmail(email)) {

            throw new UserAlreadyExistsException(
                    "Email is already registered"
            );
        }

        User user = User.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(request.password()))
                .firstName(request.firstName().trim())
                .lastName(request.lastName().trim())
                .role(Role.USER)
                .status(UserStatus.ACTIVE)
                .build();

        userRepository.save(user);
    }

    public AuthResponse login(LoginRequest request) {

        String email = normalizeEmail(request.email());

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));


        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {

            throw new InvalidCredentialsException("Invalid email or password");
        }

        validateUserCanAuthenticate(user);

        String accessToken = jwtService.generateAccessToken(user);

        String refreshToken = refreshTokenService.createRefreshToken(user.getId());

        return new AuthResponse(
                accessToken,
                refreshToken,
                "Bearer"
        );
    }

    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {

        RefreshTokenRotationResult rotationResult =
                refreshTokenService.rotateRefreshToken(request.refreshToken());


        User user = userRepository
                .findById(rotationResult.userId())
                .orElseThrow(() -> new InvalidCredentialsException("User account no longer exists"));


        try {

            validateUserCanAuthenticate(user);

        } catch (RuntimeException exception) {

            refreshTokenService.revokeAllRefreshTokens(user.getId());

            throw exception;
        }


        String accessToken = jwtService.generateAccessToken(user);

        return new AuthResponse(
                accessToken,
                rotationResult.refreshToken(),
                "Bearer"
        );
    }


    public void forgotPassword(ForgotPasswordRequest request) {

        /*
         * TODO:
         *
         * 1. Find user by email
         * 2. Generate secure reset token
         * 3. Store hashed reset token
         * 4. Set expiration
         * 5. Publish password reset event
         *    to RabbitMQ
         * 6. Notification Service
         *    sends reset email
         */


    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {

        /*
         * TODO:
         *
         * 1. Validate reset token
         * 2. Find associated user
         * 3. Validate user status
         * 4. Encode new password
         * 5. Update password
         * 6. Invalidate reset token
         * 7. Revoke all refresh tokens
         * 8. Publish notification event
         */


    }



    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }


    private void validateUserCanAuthenticate(User user) {

        if (user.getStatus() != UserStatus.ACTIVE) {

            throw new InvalidCredentialsException("User account is not active");
        }
    }
}