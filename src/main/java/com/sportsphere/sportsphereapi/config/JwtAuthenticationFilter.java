package com.sportsphere.sportsphereapi.config;

import com.sportsphere.sportsphereapi.exception.CustomException;
import com.sportsphere.sportsphereapi.user.User;
import com.sportsphere.sportsphereapi.user.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String token = jwtService.resolveToken(request);

        try {
            if (token != null && jwtService.validateToken(token, false)) {
                String userEmail = jwtService.extractUsername(token, false);
                User user = userRepository.findByEmail(userEmail)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        user, null, user.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                log.info("JWT Validation successful for: {}", user.getUsername());
            }
        } catch (CustomException ex) {
            SecurityContextHolder.clearContext();
            throw (ex);
        }

        filterChain.doFilter(request, response);
    }
}
