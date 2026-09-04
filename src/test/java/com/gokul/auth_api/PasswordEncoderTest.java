package com.gokul.auth_api;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderTest {

    @Test
    void testPasswordEncoding() {

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String password = "Test@123";

        String encodedPassword = encoder.encode(password);

        System.out.println("Encoded Password: " + encodedPassword);

        boolean matches = encoder.matches(password, encodedPassword);

        System.out.println("Password matches: " + matches);
    }
}