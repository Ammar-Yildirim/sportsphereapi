package com.sportsphere.sportsphereapi.auth;

import com.sportsphere.sportsphereapi.config.JwtService;
import com.sportsphere.sportsphereapi.exception.CustomException;
import com.sportsphere.sportsphereapi.user.Role;
import com.sportsphere.sportsphereapi.user.User;
import com.sportsphere.sportsphereapi.user.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.apache.commons.validator.routines.EmailValidator;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request, HttpServletResponse response) {
        if (!EmailValidator.getInstance().isValid(request.getEmail())) {
            throw new IllegalArgumentException("Invalid email format: " + request.getEmail());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException("Non-Unique email address Error", "This email address is already in use", HttpStatus.CONFLICT);
        }

        User user = User.builder()
                .firstname(request.getFirstName())
                .lastname(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .enabled(true)
                .build();
        user = userRepository.save(user);

        String newAccessToken = jwtService.generateToken(user, false);
        String newRefreshToken = jwtService.generateToken(user, true);

        setRefreshTokenCookie(response, newRefreshToken);

        return AuthenticationResponse.builder()
                .token(newAccessToken)
                .userId(user.getId())
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request, HttpServletResponse response) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();

        String newAccessToken = jwtService.generateToken(user, false);
        String newRefreshToken = jwtService.generateToken(user, true);
        setRefreshTokenCookie(response, newRefreshToken);

        return AuthenticationResponse.builder()
                .token(newAccessToken)
                .userId(user.getId())
                .build();
    }

    public AuthenticationResponse refresh(String refreshToken) {
        if (refreshToken == null || !jwtService.validateToken(refreshToken, true)) {
            throw new CustomException("JWT Validation Error", "Invalid or expired refresh token", HttpStatus.UNAUTHORIZED);
        }

        String username = jwtService.extractUsername(refreshToken, true);
        User user = userRepository.findByEmail(username).orElseThrow(() -> new CustomException("User not found Error", "User not found", HttpStatus.NOT_FOUND));

        String newAccessToken = jwtService.generateToken(user, false);

        return AuthenticationResponse.builder()
                .token(newAccessToken)
                .userId(user.getId())
                .build();
    }

    public String logout(String refreshToken, HttpServletResponse response) {
        if (refreshToken != null) {
            removeRefreshTokenCookie(response);
            return "Successfully logged out";
        }

        return "Error occurred during log out";
    }

    private void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie("refresh_token", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 15);
        response.addCookie(cookie);
    }

    private void removeRefreshTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("refresh_token", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

}
