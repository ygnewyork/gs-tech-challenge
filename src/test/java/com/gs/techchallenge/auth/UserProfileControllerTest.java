package com.gs.techchallenge.auth;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserProfileControllerTest {

    @Mock
    private InMemoryUserService userService;

    @InjectMocks
    private UserProfileController controller;

    @Test
    void getProfileReturnsProfileForAuthenticatedUser() {
        UserDetails userDetails = User.builder()
                .username("testuser")
                .password("encoded")
                .authorities(List.of())
                .build();
        UserProfile profile = new UserProfile("testuser", "John", "Doe", Instant.now());
        when(userService.getProfile("testuser")).thenReturn(profile);

        ResponseEntity<UserProfile> response = controller.getProfile(userDetails);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getUsername()).isEqualTo("testuser");
        assertThat(response.getBody().getFirstName()).isEqualTo("John");
        assertThat(response.getBody().getLastName()).isEqualTo("Doe");
    }
}
