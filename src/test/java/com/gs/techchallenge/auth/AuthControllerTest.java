package com.gs.techchallenge.auth;

import com.gs.techchallenge.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private InMemoryUserService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthController authController;

    @Test
    void registerReturnsCreatedWithToken() {
        UserDetails user = User.builder()
                .username("newuser")
                .password("encoded")
                .authorities(List.of())
                .build();
        when(userService.userExists("newuser")).thenReturn(false);
        when(userService.register("newuser", "password123", "John", "Doe")).thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn("jwt-token");

        ResponseEntity<AuthResponse> response = authController.register(
                new RegisterRequest("newuser", "password123", "John", "Doe")
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().token()).isEqualTo("jwt-token");
        assertThat(response.getBody().firstName()).isEqualTo("John");
        assertThat(response.getBody().lastName()).isEqualTo("Doe");
    }

    @Test
    void registerThrowsConflictForExistingUser() {
        when(userService.userExists("existing")).thenReturn(true);

        assertThatThrownBy(() -> authController.register(
                new RegisterRequest("existing", "password123", "John", "Doe")
        ))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode())
                        .isEqualTo(HttpStatus.CONFLICT));
    }

    @Test
    void loginReturnsTokenForValidCredentials() {
        UserDetails user = User.builder()
                .username("testuser")
                .password("encoded")
                .authorities(List.of())
                .build();
        UserProfile profile = new UserProfile("testuser", "Jane", "Smith", Instant.now());
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userService.loadUserByUsername("testuser")).thenReturn(user);
        when(userService.getProfile("testuser")).thenReturn(profile);
        when(jwtService.generateToken(user)).thenReturn("jwt-token");

        ResponseEntity<AuthResponse> response = authController.login(
                new AuthRequest("testuser", "password123")
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().token()).isEqualTo("jwt-token");
        assertThat(response.getBody().firstName()).isEqualTo("Jane");
        assertThat(response.getBody().lastName()).isEqualTo("Smith");
    }

    @Test
    void loginThrowsUnauthorizedForBadCredentials() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("bad"));

        assertThatThrownBy(() -> authController.login(
                new AuthRequest("testuser", "wrongpass")
        ))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode())
                        .isEqualTo(HttpStatus.UNAUTHORIZED));
    }
}
