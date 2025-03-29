package vn.java.EcommerceWeb.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.java.EcommerceWeb.dto.request.ResetPasswordRequest;
import vn.java.EcommerceWeb.dto.request.SignInRequest;
import vn.java.EcommerceWeb.dto.response.TokenResponse;
import vn.java.EcommerceWeb.service.AuthenticationService;

@RestController
@RequestMapping("/v1/api/auth")
@Validated
@Slf4j
@Tag(name = "Authentication Controller",
     description = "APIs for authentication")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid SignInRequest request) {
        try {
            TokenResponse tokenResponse = authenticationService.accessToken(request);
            return new ResponseEntity<>(tokenResponse, HttpStatus.OK);
        }catch (Exception e) {
            log.error("Error: {}", e.getMessage(), e.getCause());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> reFreshToken(HttpServletRequest request) {
        try {
            TokenResponse tokenResponse = authenticationService.refreshToken(request);
            return new ResponseEntity<>(tokenResponse, HttpStatus.OK);
        }catch (Exception e) {
            log.error("Error: {}", e.getMessage(), e.getCause());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    //Remove token
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        try {
            String message = authenticationService.logout(request);
            return new ResponseEntity<>(message, HttpStatus.OK);
        }catch (Exception e) {
            log.error("Error: {}", e.getMessage(), e.getCause());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // Forgot password
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody String email) throws MessagingException {
        try {
            String message = authenticationService.forgotPassword(email);
            return new ResponseEntity<>(message, HttpStatus.OK);
        }catch (Exception e) {
            log.error("Error: {}", e.getMessage(), e.getCause());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // Reset password
    @PostMapping("/confirm-reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody String secretKey) {
        try {
            String message = authenticationService.resetPassword(secretKey);
            return new ResponseEntity<>(message, HttpStatus.OK);
        }catch (Exception e) {
            log.error("Error: {}", e.getMessage(), e.getCause());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    //Change password
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ResetPasswordRequest request) {
        try {
            String message = authenticationService.changePassword(request);
            return new ResponseEntity<>(message, HttpStatus.OK);
        }catch (Exception e) {
            log.error("Error: {}", e.getMessage(), e.getCause());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
