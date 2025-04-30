package com.sportsphere.sportsphereapi.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private AuthenticationController authenticationController;

    private ObjectMapper objectMapper;
    private RegisterRequest registerRequest;
    private AuthenticationRequest authRequest;
    private AuthenticationResponse authResponse;
    private UUID userId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authenticationController).build();
        objectMapper = new ObjectMapper();

        userId = UUID.randomUUID();
        registerRequest = new RegisterRequest();
        registerRequest.setFirstName("John");
        registerRequest.setLastName("Doe");
        registerRequest.setEmail("john.doe@example.com");
        registerRequest.setPassword("password");

        authRequest = new AuthenticationRequest();
        authRequest.setEmail("john.doe@example.com");
        authRequest.setPassword("password");

        authResponse = new AuthenticationResponse();
        authResponse.setToken("accessToken");
        authResponse.setUserId(userId);
    }

    @Test
    void testRegister_Success() throws Exception {
        when(authenticationService.register(any(RegisterRequest.class), any())).thenReturn(authResponse);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("accessToken"))
                .andExpect(jsonPath("$.userId").value(userId.toString()));
        verify(authenticationService).register(any(RegisterRequest.class), any());
    }

    @Test
    void testAuthenticate_Success() throws Exception {
        when(authenticationService.authenticate(any(AuthenticationRequest.class), any())).thenReturn(authResponse);

        mockMvc.perform(post("/api/v1/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("accessToken"))
                .andExpect(jsonPath("$.userId").value(userId.toString()));
        verify(authenticationService).authenticate(any(AuthenticationRequest.class), any());
    }

    @Test
    void testRefresh_Success() throws Exception {
        when(authenticationService.refresh("refreshToken")).thenReturn(authResponse);

        mockMvc.perform(get("/api/v1/auth/refresh")
                        .cookie(new Cookie("refresh_token", "refreshToken")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("accessToken"))
                .andExpect(jsonPath("$.userId").value(userId.toString()));
        verify(authenticationService).refresh("refreshToken");
    }

    @Test
    void testLogout_Success() throws Exception {
        when(authenticationService.logout(eq("refreshToken"), any())).thenReturn("Successfully logged out");

        mockMvc.perform(get("/api/v1/auth/logout")
                        .cookie(new Cookie("refresh_token", "refreshToken")))
                .andExpect(status().isOk())
                .andExpect(content().string("Successfully logged out"));
        verify(authenticationService).logout(eq("refreshToken"), any());
    }
}