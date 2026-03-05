package com.gs.techchallenge.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gs.techchallenge.auth.AuthRequest;
import com.gs.techchallenge.auth.AuthResponse;
import com.gs.techchallenge.auth.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void mutualFundsEndpointIsPublic() throws Exception {
        mockMvc.perform(get("/api/mutual-funds"))
                .andExpect(status().isOk());
    }

    @Test
    void investmentsEndpointRequiresAuth() throws Exception {
        mockMvc.perform(get("/api/investments/future-value")
                        .param("ticker", "VFIAX")
                        .param("principal", "10000")
                        .param("years", "5"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void registerReturnsToken() throws Exception {
        RegisterRequest request = new RegisterRequest("integrationuser", "password123", "John", "Doe");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    void registerAndLoginFlow() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("flowuser", "password123", "Jane", "Smith");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        AuthRequest loginRequest = new AuthRequest("flowuser", "password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("Smith"));
    }

    @Test
    void loginWithBadCredentialsReturns401() throws Exception {
        AuthRequest request = new AuthRequest("nonexistent", "wrongpass");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void authenticatedUserCanAccessInvestments() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("authuser", "password123", "Auth", "User");
        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String token = objectMapper.readValue(
                result.getResponse().getContentAsString(), AuthResponse.class
        ).token();

        mockMvc.perform(get("/api/investments/future-value")
                        .header("Authorization", "Bearer " + token)
                        .param("ticker", "VFIAX")
                        .param("principal", "10000")
                        .param("years", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ticker").value("VFIAX"));
    }

    @Test
    void invalidTokenReturns401() throws Exception {
        mockMvc.perform(get("/api/investments/future-value")
                        .header("Authorization", "Bearer invalid.token.here")
                        .param("ticker", "VFIAX")
                        .param("principal", "10000")
                        .param("years", "5"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void duplicateRegistrationReturns409() throws Exception {
        RegisterRequest request = new RegisterRequest("dupeuser", "password123", "Dupe", "User");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void userProfileEndpointRequiresAuth() throws Exception {
        mockMvc.perform(get("/api/user/profile"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void authenticatedUserCanAccessProfile() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("profileuser", "password123", "Profile", "User");
        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String token = objectMapper.readValue(
                result.getResponse().getContentAsString(), AuthResponse.class
        ).token();

        mockMvc.perform(get("/api/user/profile")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("profileuser"))
                .andExpect(jsonPath("$.firstName").value("Profile"))
                .andExpect(jsonPath("$.lastName").value("User"));
    }
}
