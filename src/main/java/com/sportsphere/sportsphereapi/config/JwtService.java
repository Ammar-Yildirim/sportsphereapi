package com.sportsphere.sportsphereapi.config;

import com.sportsphere.sportsphereapi.exception.CustomException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;


import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Service
public class JwtService {

    @Value("${ACCESS_TOKEN_SECRET_KEY}")
    private String ACCESS_TOKEN_SECRET_KEY;

    @Value("${REFRESH_TOKEN_SECRET_KEY}")
    private String REFRESH_TOKEN_SECRET_KEY;

    @Value("${ACCESS_TOKEN_EXPIRATION}")
    private long ACCESS_TOKEN_EXPIRATION;

    @Value("${REFRESH_TOKEN_EXPIRATION}")
    private long REFRESH_TOKEN_EXPIRATION;


    private SecretKey getSignInKey(String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String resolveToken(HttpServletRequest request) {
        final String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public String extractUsername(String token, boolean isRefreshToken) {
        return extractClaim(token, Claims::getSubject, isRefreshToken);
    }

    private Claims extractAllClaims(String token, boolean isRefreshToken) {
        String secretKey = isRefreshToken ? REFRESH_TOKEN_SECRET_KEY : ACCESS_TOKEN_SECRET_KEY;

        return Jwts.parser()
                .verifyWith(getSignInKey(secretKey))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver, boolean isRefreshToken) {
        final Claims claims = extractAllClaims(token, isRefreshToken);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails, Boolean isRefreshToken) {
        return generateToken(new HashMap<>(), userDetails, isRefreshToken);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails, boolean isRefreshToken) {
        String secretKey = isRefreshToken ? REFRESH_TOKEN_SECRET_KEY : ACCESS_TOKEN_SECRET_KEY;
        long expiration = isRefreshToken ? REFRESH_TOKEN_EXPIRATION : ACCESS_TOKEN_EXPIRATION;

        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(secretKey))
                .compact();
    }

    public boolean validateToken(String token, boolean isRefreshToken) {
        String secretKey = isRefreshToken ? REFRESH_TOKEN_SECRET_KEY : ACCESS_TOKEN_SECRET_KEY;

        try {
            Jwts
                    .parser()
                    .verifyWith(getSignInKey(secretKey))
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (IllegalArgumentException e){
            throw new CustomException("JWT Token not provided", HttpStatus.UNAUTHORIZED);
        } catch (JwtException e) {
            throw new CustomException(String.format("Expired or invalid %s JWT token %s", isRefreshToken ? "refresh" : "access", token), HttpStatus.UNAUTHORIZED);
        }
    }
}
