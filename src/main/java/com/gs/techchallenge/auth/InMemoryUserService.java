package com.gs.techchallenge.auth;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InMemoryUserService implements UserDetailsService {

    private final ConcurrentHashMap<String, UserDetails> users = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, UserProfile> profiles = new ConcurrentHashMap<>();
    private final PasswordEncoder passwordEncoder;

    public InMemoryUserService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public UserDetails register(String username, String rawPassword, String firstName, String lastName) {
        if (users.containsKey(username)) {
            throw new IllegalArgumentException("Username already exists: " + username);
        }
        UserDetails user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(rawPassword))
                .authorities(List.of())
                .build();
        users.put(username, user);
        profiles.put(username, new UserProfile(username, firstName, lastName, Instant.now()));
        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails user = users.get(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        return user;
    }

    public UserProfile getProfile(String username) {
        UserProfile profile = profiles.get(username);
        if (profile == null) {
            throw new UsernameNotFoundException("Profile not found: " + username);
        }
        return profile;
    }

    public boolean userExists(String username) {
        return users.containsKey(username);
    }
}
