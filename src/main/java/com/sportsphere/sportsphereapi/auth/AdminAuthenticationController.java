package com.sportsphere.sportsphereapi.auth;

import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/auth")
@RequiredArgsConstructor
public class AdminAuthenticationController {

    private final AdminAuthenticationService adminAuthenticationService;

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request, @NonNull HttpServletResponse response) {
        return ResponseEntity.ok(adminAuthenticationService.authenticateAdmin(request, response));
    }

    @GetMapping("/refresh")
    public ResponseEntity<AuthenticationResponse> refresh(@CookieValue("admin_refresh_token") String refreshToken) {
        return ResponseEntity.ok(adminAuthenticationService.refreshAdmin(refreshToken));
    }

    @GetMapping("/logout")
    public ResponseEntity<String> logout(@CookieValue("admin_refresh_token") String refreshToken, @NonNull HttpServletResponse response) {
        return ResponseEntity.ok(adminAuthenticationService.logoutAdmin(refreshToken, response));
    }
}