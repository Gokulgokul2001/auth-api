package com.gokul.auth_api.service;

import com.gokul.auth_api.dto.LoginRequest;
import com.gokul.auth_api.dto.RegisterRequest;
import com.gokul.auth_api.exception.EmailAlreadyExistsException;
import com.gokul.auth_api.exception.InvalidCredentialsException;
import com.gokul.auth_api.model.User;
import com.gokul.auth_api.repository.UserRepository;
import com.gokul.auth_api.security.JwtService;
import org.junit.jupiter.api.Test;
import com.gokul.auth_api.dto.LoginResponse;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Test
    void registerUser_shouldCreateUserSuccessfully() {

        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        JwtService jwtService = mock(JwtService.class);

        AuthService authService =
                new AuthService(
                        userRepository,
                        passwordEncoder,
                        jwtService
                );

        RegisterRequest request =
                new RegisterRequest(
                        "testuser",
                        "test@gmail.com",
                        "password123"
                );

        when(userRepository.existsByEmail("test@gmail.com"))
                .thenReturn(false);

        when(passwordEncoder.encode("password123"))
                .thenReturn("encryptedPassword");

        User savedUser = new User();

        savedUser.setId(1L);
        savedUser.setUsername("testuser");
        savedUser.setEmail("test@gmail.com");
        savedUser.setPassword("encryptedPassword");
        savedUser.setRole("USER");

        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        User result = authService.register(request);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@gmail.com", result.getEmail());
        assertEquals("encryptedPassword", result.getPassword());
        assertEquals("USER", result.getRole());

        verify(userRepository).existsByEmail("test@gmail.com");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
    }
    @Test
    void registerUser_shouldThrowException_whenEmailAlreadyExists() {

        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        JwtService jwtService = mock(JwtService.class);

        AuthService authService =
                new AuthService(
                        userRepository,
                        passwordEncoder,
                        jwtService
                );

        RegisterRequest request =
                new RegisterRequest(
                        "testuser",
                        "existing@gmail.com",
                        "password123"
                );

        when(userRepository.existsByEmail("existing@gmail.com"))
                .thenReturn(true);

        assertThrows(
                EmailAlreadyExistsException.class,
                () -> authService.register(request)
        );

        verify(userRepository).existsByEmail("existing@gmail.com");

        verify(userRepository, never())
                .save(any(User.class));

        verify(passwordEncoder, never())
                .encode(anyString());
    }
    @Test
    void loginUser_shouldThrowException_whenPasswordIsIncorrect() {

        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        JwtService jwtService = mock(JwtService.class);

        AuthService authService =
                new AuthService(
                        userRepository,
                        passwordEncoder,
                        jwtService
                );

        LoginRequest request =
                new LoginRequest(
                        "test@gmail.com",
                        "wrongpassword"
                );

        User user = new User();

        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@gmail.com");
        user.setPassword("encryptedPassword");
        user.setRole("USER");

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(java.util.Optional.of(user));

        when(passwordEncoder.matches(
                "wrongpassword",
                "encryptedPassword"
        )).thenReturn(false);

        assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login(request)
        );

        verify(userRepository).findByEmail("test@gmail.com");

        verify(passwordEncoder).matches(
                "wrongpassword",
                "encryptedPassword"
        );

        verify(jwtService, never())
                .generateToken(anyString());
    }
    @Test
    void loginUser_shouldReturnLoginResponse_whenCredentialsAreCorrect() {

        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        JwtService jwtService = mock(JwtService.class);

        AuthService authService =
                new AuthService(
                        userRepository,
                        passwordEncoder,
                        jwtService
                );

        LoginRequest request =
                new LoginRequest(
                        "test@gmail.com",
                        "password123"
                );

        User user = new User();

        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@gmail.com");
        user.setPassword("encryptedPassword");
        user.setRole("USER");

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(java.util.Optional.of(user));

        when(passwordEncoder.matches(
                "password123",
                "encryptedPassword"
        )).thenReturn(true);

        when(jwtService.generateToken("test@gmail.com"))
                .thenReturn("test-jwt-token");

        LoginResponse result = authService.login(request);

        assertNotNull(result);
        assertEquals("test-jwt-token", result.getToken());
        assertEquals("testuser", result.getUsername());
        assertEquals("test@gmail.com", result.getEmail());
        assertEquals("USER", result.getRole());

        verify(userRepository).findByEmail("test@gmail.com");

        verify(passwordEncoder).matches(
                "password123",
                "encryptedPassword"
        );

        verify(jwtService).generateToken("test@gmail.com");
    }
    @Test
    void loginUser_shouldThrowException_whenUserDoesNotExist() {

        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        JwtService jwtService = mock(JwtService.class);

        AuthService authService =
                new AuthService(
                        userRepository,
                        passwordEncoder,
                        jwtService
                );

        LoginRequest request =
                new LoginRequest(
                        "unknown@gmail.com",
                        "password123"
                );

        when(userRepository.findByEmail("unknown@gmail.com"))
                .thenReturn(java.util.Optional.empty());

        assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login(request)
        );

        verify(userRepository).findByEmail("unknown@gmail.com");

        verify(passwordEncoder, never())
                .matches(anyString(), anyString());

        verify(jwtService, never())
                .generateToken(anyString());
    }
}