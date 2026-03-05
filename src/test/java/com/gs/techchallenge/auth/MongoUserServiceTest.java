package com.gs.techchallenge.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MongoUserServiceTest {

    @Mock
    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;
    private MongoUserService userService;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        userService = new MongoUserService(userRepository, passwordEncoder);
    }

    @Test
    void registerCreatesNewUser() {
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.save(any(UserDocument.class))).thenAnswer(inv -> inv.getArgument(0));

        UserDetails user = userService.register("testuser", "password123", "John", "Doe");

        assertThat(user.getUsername()).isEqualTo("testuser");
        assertThat(passwordEncoder.matches("password123", user.getPassword())).isTrue();
        verify(userRepository).save(any(UserDocument.class));
    }

    @Test
    void registerThrowsExceptionForDuplicateUsername() {
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        assertThatThrownBy(() -> userService.register("testuser", "otherpass", "Jane", "Smith"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void loadUserByUsernameReturnsRegisteredUser() {
        UserDocument doc = new UserDocument("testuser", "encoded", "John", "Doe", Instant.now());
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(doc));

        UserDetails user = userService.loadUserByUsername("testuser");

        assertThat(user.getUsername()).isEqualTo("testuser");
    }

    @Test
    void loadUserByUsernameThrowsForUnknownUser() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.loadUserByUsername("unknown"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void userExistsReturnsTrueForRegisteredUser() {
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        assertThat(userService.userExists("testuser")).isTrue();
    }

    @Test
    void userExistsReturnsFalseForUnknownUser() {
        when(userRepository.existsByUsername("unknown")).thenReturn(false);

        assertThat(userService.userExists("unknown")).isFalse();
    }

    @Test
    void getProfileReturnsStoredProfile() {
        UserDocument doc = new UserDocument("testuser", "encoded", "John", "Doe", Instant.now());
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(doc));

        UserProfile profile = userService.getProfile("testuser");

        assertThat(profile.getUsername()).isEqualTo("testuser");
        assertThat(profile.getFirstName()).isEqualTo("John");
        assertThat(profile.getLastName()).isEqualTo("Doe");
        assertThat(profile.getCreatedAt()).isNotNull();
    }

    @Test
    void getProfileThrowsForUnknownUser() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getProfile("unknown"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("Profile not found");
    }
}
