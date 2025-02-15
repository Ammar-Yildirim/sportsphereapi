package com.sportsphere.sportsphereapi.auth;

import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request,@NonNull HttpServletResponse response){
        return ResponseEntity.ok(authenticationService.register(request, response));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody AuthenticationRequest request,@NonNull HttpServletResponse response){
        return ResponseEntity.ok(authenticationService.authenticate(request, response));
    }

    @GetMapping("/refresh")
    public ResponseEntity<AuthenticationResponse> refresh(@CookieValue("refresh_token") String refreshToken){
        return ResponseEntity.ok(authenticationService.refresh(refreshToken));
    }

    @GetMapping("/logout")
    public ResponseEntity<String> logout(@CookieValue("refresh_token") String refreshToken,@NonNull HttpServletResponse response){
        return ResponseEntity.ok(authenticationService.logout(refreshToken, response));
    }
}
