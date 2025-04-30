package com.sportsphere.sportsphereapi.auth;

import com.sportsphere.sportsphereapi.config.JwtService;
import com.sportsphere.sportsphereapi.exception.CustomException;
import com.sportsphere.sportsphereapi.user.Role;
import com.sportsphere.sportsphereapi.user.User;
import com.sportsphere.sportsphereapi.user.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private AuthenticationService authenticationService;

    private RegisterRequest registerRequest;
    private AuthenticationRequest authenticationRequest;
    private User testUser;
    private final String ACCESS_TOKEN = "test-access-token";
    private final String REFRESH_TOKEN = "test-refresh-token";
    private final UUID USER_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setFirstName("John");
        registerRequest.setLastName("Doe");
        registerRequest.setEmail("john.doe@example.com");
        registerRequest.setPassword("password123");

        authenticationRequest = new AuthenticationRequest();
        authenticationRequest.setEmail("john.doe@example.com");
        authenticationRequest.setPassword("password123");

        testUser = User.builder()
                .id(USER_ID)
                .firstname("John")
                .lastname("Doe")
                .email("john.doe@example.com")
                .password("encoded_password")
                .role(Role.USER)
                .enabled(true)
                .build();
    }

    @Test
    void registerSuccessful() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtService.generateToken(any(User.class), eq(false))).thenReturn(ACCESS_TOKEN);
        when(jwtService.generateToken(any(User.class), eq(true))).thenReturn(REFRESH_TOKEN);

        AuthenticationResponse result = authenticationService.register(registerRequest, response);

        assertNotNull(result);
        assertEquals(ACCESS_TOKEN, result.getToken());
        assertEquals(USER_ID, result.getUserId());

        verify(userRepository).existsByEmail(registerRequest.getEmail());
        verify(passwordEncoder).encode(registerRequest.getPassword());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertEquals(registerRequest.getFirstName(), savedUser.getFirstname());
        assertEquals(registerRequest.getLastName(), savedUser.getLastname());
        assertEquals(registerRequest.getEmail(), savedUser.getEmail());

        verify(jwtService).generateToken(any(User.class), eq(false));
        verify(jwtService).generateToken(any(User.class), eq(true));
        verify(response).addCookie(any(Cookie.class));
    }

    @Test
    void registerWithInvalidEmailFormat() {
        registerRequest.setEmail("invalid-email");

        assertThrows(IllegalArgumentException.class, () ->
                authenticationService.register(registerRequest, response)
        );

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerWithExistingEmail() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        CustomException exception = assertThrows(CustomException.class, () ->
                authenticationService.register(registerRequest, response)
        );

        assertEquals("Non-Unique email address Error", exception.getError());
        assertEquals("This email address is already in use", exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getHttpStatus());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void authenticateSuccessful() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(jwtService.generateToken(any(User.class), eq(false))).thenReturn(ACCESS_TOKEN);
        when(jwtService.generateToken(any(User.class), eq(true))).thenReturn(REFRESH_TOKEN);

        AuthenticationResponse result = authenticationService.authenticate(authenticationRequest, response);

        assertNotNull(result);
        assertEquals(ACCESS_TOKEN, result.getToken());
        assertEquals(USER_ID, result.getUserId());

        verify(authenticationManager).authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        );
        verify(userRepository).findByEmail(authenticationRequest.getEmail());
        verify(jwtService).generateToken(testUser, false);
        verify(jwtService).generateToken(testUser, true);
        verify(response).addCookie(any(Cookie.class));
    }

    @Test
    void refreshTokenSuccessful() {
        when(jwtService.validateToken(anyString(), eq(true))).thenReturn(true);
        when(jwtService.extractUsername(anyString(), eq(true))).thenReturn("john.doe@example.com");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(jwtService.generateToken(any(User.class), eq(false))).thenReturn(ACCESS_TOKEN);

        AuthenticationResponse result = authenticationService.refresh(REFRESH_TOKEN);

        assertNotNull(result);
        assertEquals(ACCESS_TOKEN, result.getToken());
        assertEquals(USER_ID, result.getUserId());

        verify(jwtService).validateToken(REFRESH_TOKEN, true);
        verify(jwtService).extractUsername(REFRESH_TOKEN, true);
        verify(userRepository).findByEmail("john.doe@example.com");
        verify(jwtService).generateToken(testUser, false);
    }

    @Test
    void refreshWithInvalidToken() {
        when(jwtService.validateToken(anyString(), eq(true))).thenReturn(false);

        CustomException exception = assertThrows(CustomException.class, () ->
                authenticationService.refresh(REFRESH_TOKEN)
        );

        assertEquals("JWT Validation Error", exception.getError());
        assertEquals("Invalid or expired refresh token", exception.getMessage());
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getHttpStatus());
    }

    @Test
    void refreshWithNonExistentUser() {
        when(jwtService.validateToken(anyString(), eq(true))).thenReturn(true);
        when(jwtService.extractUsername(anyString(), eq(true))).thenReturn("john.doe@example.com");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () ->
                authenticationService.refresh(REFRESH_TOKEN)
        );

        assertEquals("User not found Error", exception.getError());
        assertEquals("User not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
    }

    @Test
    void logoutSuccessful() {
        String result = authenticationService.logout(REFRESH_TOKEN, response);

        assertEquals("Successfully logged out", result);
        verify(response).addCookie(any(Cookie.class));
    }

    @Test
    void logoutWithNullToken() {
        String result = authenticationService.logout(null, response);

        assertEquals("Error occurred during log out", result);
        verify(response, never()).addCookie(any(Cookie.class));
    }
}