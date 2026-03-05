package com.gs.techchallenge.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InMemoryUserServiceTest {

    private InMemoryUserService userService;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        userService = new InMemoryUserService(passwordEncoder);
    }

    @Test
    void registerCreatesNewUser() {
        UserDetails user = userService.register("testuser", "password123", "John", "Doe");

        assertThat(user.getUsername()).isEqualTo("testuser");
        assertThat(passwordEncoder.matches("password123", user.getPassword())).isTrue();
    }

    @Test
    void registerThrowsExceptionForDuplicateUsername() {
        userService.register("testuser", "password123", "John", "Doe");

        assertThatThrownBy(() -> userService.register("testuser", "otherpass", "Jane", "Smith"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void loadUserByUsernameReturnsRegisteredUser() {
        userService.register("testuser", "password123", "John", "Doe");

        UserDetails user = userService.loadUserByUsername("testuser");

        assertThat(user.getUsername()).isEqualTo("testuser");
    }

    @Test
    void loadUserByUsernameThrowsForUnknownUser() {
        assertThatThrownBy(() -> userService.loadUserByUsername("unknown"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void userExistsReturnsTrueForRegisteredUser() {
        userService.register("testuser", "password123", "John", "Doe");

        assertThat(userService.userExists("testuser")).isTrue();
    }

    @Test
    void userExistsReturnsFalseForUnknownUser() {
        assertThat(userService.userExists("unknown")).isFalse();
    }

    @Test
    void getProfileReturnsStoredProfile() {
        userService.register("testuser", "password123", "John", "Doe");

        UserProfile profile = userService.getProfile("testuser");

        assertThat(profile.getUsername()).isEqualTo("testuser");
        assertThat(profile.getFirstName()).isEqualTo("John");
        assertThat(profile.getLastName()).isEqualTo("Doe");
        assertThat(profile.getCreatedAt()).isNotNull();
    }

    @Test
    void getProfileThrowsForUnknownUser() {
        assertThatThrownBy(() -> userService.getProfile("unknown"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("Profile not found");
    }
}
