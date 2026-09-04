package com.gokul.auth_api.security;

import com.gokul.auth_api.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    @Test
    void doFilter_shouldContinue_whenAuthorizationHeaderIsMissing()
            throws Exception {

        JwtService jwtService = mock(JwtService.class);
        CustomUserDetailsService userDetailsService =
                mock(CustomUserDetailsService.class);

        JwtAuthenticationFilter filter =
                new JwtAuthenticationFilter(
                        jwtService,
                        userDetailsService
                );

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        FilterChain filterChain = mock(FilterChain.class);

        SecurityContextHolder.clearContext();

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);

        verifyNoInteractions(jwtService);
        verifyNoInteractions(userDetailsService);
    }
    @Test
    void doFilter_shouldAuthenticateUser_whenTokenIsValid()
            throws Exception {

        JwtService jwtService = mock(JwtService.class);

        CustomUserDetailsService userDetailsService =
                mock(CustomUserDetailsService.class);

        JwtAuthenticationFilter filter =
                new JwtAuthenticationFilter(
                        jwtService,
                        userDetailsService
                );

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.addHeader(
                "Authorization",
                "Bearer valid-jwt-token"
        );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        FilterChain filterChain = mock(FilterChain.class);

        org.springframework.security.core.userdetails.UserDetails userDetails =
                org.springframework.security.core.userdetails.User
                        .withUsername("gokul@gmail.com")
                        .password("password")
                        .roles("USER")
                        .build();

        when(jwtService.extractEmail("valid-jwt-token"))
                .thenReturn("gokul@gmail.com");

        when(userDetailsService.loadUserByUsername("gokul@gmail.com"))
                .thenReturn(userDetails);

        SecurityContextHolder.clearContext();

        filter.doFilter(request, response, filterChain);

        org.springframework.security.core.Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        org.junit.jupiter.api.Assertions.assertNotNull(authentication);

        org.junit.jupiter.api.Assertions.assertEquals(
                "gokul@gmail.com",
                authentication.getName()
        );

        org.junit.jupiter.api.Assertions.assertTrue(
                authentication.getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                authority.getAuthority().equals("ROLE_USER"))
        );

        verify(jwtService).extractEmail("valid-jwt-token");

        verify(userDetailsService)
                .loadUserByUsername("gokul@gmail.com");

        verify(filterChain).doFilter(request, response);

        SecurityContextHolder.clearContext();
    }
    @Test
    void doFilter_shouldNotAuthenticateUser_whenTokenIsInvalid()
            throws Exception {

        JwtService jwtService = mock(JwtService.class);

        CustomUserDetailsService userDetailsService =
                mock(CustomUserDetailsService.class);

        JwtAuthenticationFilter filter =
                new JwtAuthenticationFilter(
                        jwtService,
                        userDetailsService
                );

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.addHeader(
                "Authorization",
                "Bearer invalid-jwt-token"
        );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        FilterChain filterChain = mock(FilterChain.class);

        when(jwtService.extractEmail("invalid-jwt-token"))
                .thenThrow(new RuntimeException("Invalid JWT token"));

        SecurityContextHolder.clearContext();

        filter.doFilter(request, response, filterChain);

        org.springframework.security.core.Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        org.junit.jupiter.api.Assertions.assertNull(authentication);

        verify(jwtService).extractEmail("invalid-jwt-token");

        verifyNoInteractions(userDetailsService);

        verify(filterChain).doFilter(request, response);

        SecurityContextHolder.clearContext();
    }
    @Test
    void doFilter_shouldContinue_whenAuthorizationHeaderDoesNotStartWithBearer()
            throws Exception {

        JwtService jwtService = mock(JwtService.class);

        CustomUserDetailsService userDetailsService =
                mock(CustomUserDetailsService.class);

        JwtAuthenticationFilter filter =
                new JwtAuthenticationFilter(
                        jwtService,
                        userDetailsService
                );

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.addHeader(
                "Authorization",
                "Basic some-token"
        );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        FilterChain filterChain = mock(FilterChain.class);

        SecurityContextHolder.clearContext();

        filter.doFilter(request, response, filterChain);

        org.springframework.security.core.Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        org.junit.jupiter.api.Assertions.assertNull(authentication);

        verifyNoInteractions(jwtService);
        verifyNoInteractions(userDetailsService);

        verify(filterChain).doFilter(request, response);

        SecurityContextHolder.clearContext();
    }
    @Test
    void doFilter_shouldContinue_whenUserIsAlreadyAuthenticated()
            throws Exception {

        JwtService jwtService = mock(JwtService.class);

        CustomUserDetailsService userDetailsService =
                mock(CustomUserDetailsService.class);

        JwtAuthenticationFilter filter =
                new JwtAuthenticationFilter(
                        jwtService,
                        userDetailsService
                );

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.addHeader(
                "Authorization",
                "Bearer valid-jwt-token"
        );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        FilterChain filterChain = mock(FilterChain.class);

        org.springframework.security.core.Authentication existingAuthentication =
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        "existing-user",
                        null,
                        java.util.List.of(
                                new org.springframework.security.core.authority.SimpleGrantedAuthority(
                                        "ROLE_USER"
                                )
                        )
                );

        SecurityContextHolder.clearContext();
        SecurityContextHolder.getContext()
                .setAuthentication(existingAuthentication);

        filter.doFilter(request, response, filterChain);

        org.springframework.security.core.Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        org.junit.jupiter.api.Assertions.assertNotNull(authentication);

        org.junit.jupiter.api.Assertions.assertEquals(
                "existing-user",
                authentication.getName()
        );

        verifyNoInteractions(jwtService);
        verifyNoInteractions(userDetailsService);

        verify(filterChain).doFilter(request, response);

        SecurityContextHolder.clearContext();
    }
}