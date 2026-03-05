package com.gs.techchallenge.auth;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class MongoUserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public MongoUserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDetails register(String username, String rawPassword, String firstName, String lastName) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists: " + username);
        }
        UserDocument doc = new UserDocument(
                username,
                passwordEncoder.encode(rawPassword),
                firstName,
                lastName,
                Instant.now()
        );
        userRepository.save(doc);
        return User.builder()
                .username(doc.getUsername())
                .password(doc.getPassword())
                .authorities(List.of())
                .build();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDocument doc = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return User.builder()
                .username(doc.getUsername())
                .password(doc.getPassword())
                .authorities(List.of())
                .build();
    }

    public UserProfile getProfile(String username) {
        UserDocument doc = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Profile not found: " + username));
        return new UserProfile(doc.getUsername(), doc.getFirstName(), doc.getLastName(), doc.getCreatedAt());
    }

    public boolean userExists(String username) {
        return userRepository.existsByUsername(username);
    }
}
