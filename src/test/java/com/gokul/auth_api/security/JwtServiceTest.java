package com.gokul.auth_api.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
        "jwt.secret=test-secret-key-for-jwt-testing-only-1234567890",
        "jwt.expiration=3600000"
})
class JwtServiceTest {

    @Autowired
    private JwtService jwtService;

    @Test
    void generateToken_shouldCreateValidToken() {

        String email = "test@gmail.com";

        String token = jwtService.generateToken(email);

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void extractEmail_shouldReturnEmailFromToken() {

        String email = "test@gmail.com";

        String token = jwtService.generateToken(email);

        String extractedEmail = jwtService.extractEmail(token);

        assertEquals(email, extractedEmail);
    }
    @Test
    void extractEmail_shouldRejectInvalidToken() {

        String invalidToken = "this.is.an.invalid.token";

        assertThrows(
                Exception.class,
                () -> jwtService.extractEmail(invalidToken)
        );
    }
}