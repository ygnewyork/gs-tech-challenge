package com.gs.techchallenge.auth;

import java.time.Instant;

public class UserProfile {

    private final String username;
    private final String firstName;
    private final String lastName;
    private final Instant createdAt;

    public UserProfile(String username, String firstName, String lastName, Instant createdAt) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.createdAt = createdAt;
    }

    public String getUsername() { return username; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public Instant getCreatedAt() { return createdAt; }
}
