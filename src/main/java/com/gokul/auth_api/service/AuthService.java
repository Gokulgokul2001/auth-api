package com.gokul.auth_api.service;

import com.gokul.auth_api.dto.RegisterRequest;
import com.gokul.auth_api.dto.LoginRequest;
import com.gokul.auth_api.dto.LoginResponse;
import com.gokul.auth_api.dto.ForgotPasswordRequest;
import com.gokul.auth_api.dto.ResetPasswordRequest;
import com.gokul.auth_api.model.User;
import com.gokul.auth_api.repository.UserRepository;
import com.gokul.auth_api.security.JwtService;
import com.gokul.auth_api.exception.EmailAlreadyExistsException;
import com.gokul.auth_api.exception.InvalidCredentialsException;
import com.gokul.auth_api.exception.InvalidResetTokenException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;


    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public User register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already registered");
        }

        User user = new User();

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");

        return userRepository.save(user);
    }
    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new InvalidCredentialsException(
                                "Invalid email or password"));

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            throw new InvalidCredentialsException(
                    "Invalid email or password");
        }

        String token = jwtService.generateToken(user.getEmail());

        return new LoginResponse(
                token,
                user.getUsername(),
                user.getEmail(),
                user.getRole()
        );
    }
    public String forgotPassword(ForgotPasswordRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        SecureRandom secureRandom = new SecureRandom();

        byte[] tokenBytes = new byte[32];
        secureRandom.nextBytes(tokenBytes);

        String resetToken =
                Base64.getUrlEncoder()
                        .withoutPadding()
                        .encodeToString(tokenBytes);

        user.setResetToken(resetToken);

        user.setResetTokenExpiry(
                LocalDateTime.now().plusMinutes(15)
        );

        userRepository.save(user);

        return resetToken;
    }
    public void resetPassword(ResetPasswordRequest request) {

        User user = userRepository.findByResetToken(request.getToken())
                .orElseThrow(() ->
                        new InvalidResetTokenException(
                                "Invalid or expired reset token"));

        if (user.getResetTokenExpiry() == null ||
                user.getResetTokenExpiry().isBefore(
                        java.time.LocalDateTime.now())) {

            throw new InvalidResetTokenException(
                    "Invalid or expired reset token");
        }

        user.setPassword(
                passwordEncoder.encode(
                        request.getNewPassword()
                )
        );

        user.setResetToken(null);
        user.setResetTokenExpiry(null);

        userRepository.save(user);
    }
}