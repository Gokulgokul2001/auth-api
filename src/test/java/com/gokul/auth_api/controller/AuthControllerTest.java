package com.gokul.auth_api.controller;

import com.gokul.auth_api.dto.RegisterRequest;
import com.gokul.auth_api.model.User;
import com.gokul.auth_api.security.JwtService;
import com.gokul.auth_api.service.AuthService;
import com.gokul.auth_api.service.CustomUserDetailsService;
import com.gokul.auth_api.dto.LoginRequest;
import com.gokul.auth_api.dto.LoginResponse;
import com.gokul.auth_api.exception.InvalidCredentialsException;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;


    // =========================================================
    // 1. Successful Registration
    // =========================================================

    @Test
    void register_shouldReturn201_whenRequestIsValid() throws Exception {

        User user = new User();

        user.setId(1L);
        user.setUsername("Gokul");
        user.setEmail("gokul@gmail.com");
        user.setRole("USER");

        when(authService.register(any(RegisterRequest.class)))
                .thenReturn(user);

        String requestBody = """
                {
                    "username": "Gokul",
                    "email": "gokul@gmail.com",
                    "password": "password123"
                }
                """;

        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("Gokul"))
                .andExpect(jsonPath("$.email").value("gokul@gmail.com"))
                .andExpect(jsonPath("$.role").value("USER"));
    }


    // =========================================================
    // 2. Empty Username
    // =========================================================

    @Test
    void register_shouldReturn400_whenUsernameIsEmpty() throws Exception {

        String requestBody = """
                {
                    "username": "",
                    "email": "gokul@gmail.com",
                    "password": "password123"
                }
                """;

        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.username")
                        .value("Username is required"));
    }
    @Test
    void register_shouldReturn400_whenEmailIsInvalid() throws Exception {

        String requestBody = """
            {
                "username": "Gokul",
                "email": "invalid-email",
                "password": "password123"
            }
            """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.email").value("Invalid email format"));
    }
    @Test
    void register_shouldReturn400_whenPasswordIsTooShort() throws Exception {

        String requestBody = """
            {
                "username": "Gokul",
                "email": "gokul@gmail.com",
                "password": "123"
            }
            """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.password")
                        .value("Password must be at least 6 characters"));
    }
    @Test
    void login_shouldReturn200_whenCredentialsAreValid() throws Exception {

        LoginResponse loginResponse = new LoginResponse(
                "test-jwt-token",
                "Gokul",
                "gokul@gmail.com",
                "USER"
        );

        when(authService.login(any(LoginRequest.class)))
                .thenReturn(loginResponse);

        String requestBody = """
            {
                "email": "gokul@gmail.com",
                "password": "password123"
            }
            """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test-jwt-token"))
                .andExpect(jsonPath("$.username").value("Gokul"))
                .andExpect(jsonPath("$.email").value("gokul@gmail.com"))
                .andExpect(jsonPath("$.role").value("USER"));
    }
    @Test
    void login_shouldReturn401_whenCredentialsAreInvalid() throws Exception {

        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new InvalidCredentialsException("Invalid email or password"));

        String requestBody = """
            {
                "email": "gokul@gmail.com",
                "password": "wrongpassword"
            }
            """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message")
                        .value("Invalid email or password"));
    }
    @Test
    void login_shouldReturn400_whenEmailIsEmpty() throws Exception {

        String requestBody = """
            {
                "email": "",
                "password": "password123"
            }
            """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.email").value("Email is required"));
    }
    @Test
    void login_shouldReturn400_whenPasswordIsEmpty() throws Exception {

        String requestBody = """
            {
                "email": "gokul@gmail.com",
                "password": ""
            }
            """;

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.password")
                        .value("Password is required"));
    }
    
}
