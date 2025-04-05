package com.sportsphere.sportsphereapi.exception;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Map<String, String>> handleCustomException(CustomException ex) {
        return ResponseEntity.status(ex.getHttpStatus())
                .body(Map.of("error", ex.getError(), "message", ex.getMessage()));
    }

    @ExceptionHandler(MissingRequestCookieException.class)
    public ResponseEntity<Map<String, String>> handleMissingCookieException(MissingRequestCookieException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Missing Request Cookie", "message", ex.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN.value())
                .body(Map.of("error", "Access Denied Exception", "message", ex.getMessage()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value())
                .body(Map.of("error", "Bad Credentials Exception", "message", "Missing or Incorrect Credentials"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception ex) {
        ex.printStackTrace();
        log.error("Error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST.value())
                .body(Map.of("error", "Error ", "message", ex.getMessage()));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUsernameNotFoundException(Exception ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value())
                .body(Map.of("error", "User Not Found ", "Message", "User Not Found"));
    }
}
