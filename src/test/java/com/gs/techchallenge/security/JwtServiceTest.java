package com.gs.techchallenge.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(
                "test-secret-key-that-is-at-least-256-bits-long-for-hmac-sha-testing",
                3600000
        );
    }

    @Test
    void generateTokenReturnsNonEmptyString() {
        UserDetails user = User.builder()
                .username("testuser")
                .password("password")
                .authorities(List.of())
                .build();

        String token = jwtService.generateToken(user);

        assertThat(token).isNotBlank();
    }

    @Test
    void extractUsernameReturnsCorrectUsername() {
        UserDetails user = User.builder()
                .username("testuser")
                .password("password")
                .authorities(List.of())
                .build();

        String token = jwtService.generateToken(user);
        String username = jwtService.extractUsername(token);

        assertThat(username).isEqualTo("testuser");
    }

    @Test
    void isTokenValidReturnsTrueForValidToken() {
        UserDetails user = User.builder()
                .username("testuser")
                .password("password")
                .authorities(List.of())
                .build();

        String token = jwtService.generateToken(user);

        assertThat(jwtService.isTokenValid(token, user)).isTrue();
    }

    @Test
    void isTokenValidReturnsFalseForDifferentUser() {
        UserDetails user1 = User.builder()
                .username("user1")
                .password("password")
                .authorities(List.of())
                .build();
        UserDetails user2 = User.builder()
                .username("user2")
                .password("password")
                .authorities(List.of())
                .build();

        String token = jwtService.generateToken(user1);

        assertThat(jwtService.isTokenValid(token, user2)).isFalse();
    }

    @Test
    void expiredTokenIsInvalid() {
        JwtService shortLivedService = new JwtService(
                "test-secret-key-that-is-at-least-256-bits-long-for-hmac-sha-testing",
                -1000
        );
        UserDetails user = User.builder()
                .username("testuser")
                .password("password")
                .authorities(List.of())
                .build();

        String token = shortLivedService.generateToken(user);

        assertThat(shortLivedService.isTokenValid(token, user)).isFalse();
    }

    @Test
    void invalidTokenThrowsException() {
        assertThatThrownBy(() -> jwtService.extractUsername("invalid.token.here"))
                .isInstanceOf(Exception.class);
    }
}
