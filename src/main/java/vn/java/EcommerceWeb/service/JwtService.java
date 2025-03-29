package vn.java.EcommerceWeb.service;

import org.springframework.security.core.userdetails.UserDetails;
import vn.java.EcommerceWeb.enums.TokenType;

public interface JwtService {
    String generateToken(UserDetails userDetails);

    String generateRefreshToken(UserDetails userDetails);

    String generateResetPasswordToken(UserDetails userDetails);

    String extractUsername(String token, TokenType type);

    boolean isValid(String token, TokenType type, UserDetails userDetails);
}
