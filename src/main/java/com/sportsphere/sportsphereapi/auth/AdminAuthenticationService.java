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
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminAuthenticationService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse authenticateAdmin(AuthenticationRequest request, HttpServletResponse response) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException("Authentication Error", "User not found", HttpStatus.NOT_FOUND));

        if (user.getRole() != Role.ROLE_ADMIN) {
            throw new CustomException("Authorization Error", "Insufficient privileges", HttpStatus.FORBIDDEN);
        }

        String newAccessToken = jwtService.generateToken(user, false);
        String newRefreshToken = jwtService.generateToken(user, true);
        setAdminRefreshTokenCookie(response, newRefreshToken);

        return AuthenticationResponse.builder()
                .token(newAccessToken)
                .build();
    }

    public AuthenticationResponse refreshAdmin(String refreshToken) {
        if (refreshToken == null || !jwtService.validateToken(refreshToken, true)) {
            throw new CustomException("JWT Validation Error", "Invalid or expired admin refresh token", HttpStatus.UNAUTHORIZED);
        }

        String username = jwtService.extractUsername(refreshToken, true);
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new CustomException("User not found Error", "User not found", HttpStatus.NOT_FOUND));

        if (user.getRole() != Role.ROLE_ADMIN) {
            throw new CustomException("Authorization Error", "Insufficient privileges", HttpStatus.FORBIDDEN);
        }

        String newAccessToken = jwtService.generateToken(user, false);

        return AuthenticationResponse.builder()
                .token(newAccessToken)
                .build();
    }

    public String logoutAdmin(String refreshToken, HttpServletResponse response) {
        if (refreshToken != null) {
            removeAdminRefreshTokenCookie(response);
            return "Successfully logged out from admin";
        }

        return "Error occurred during admin log out";
    }

    private void setAdminRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie("admin_refresh_token", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 7);
        response.addCookie(cookie);
    }

    private void removeAdminRefreshTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("admin_refresh_token", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}