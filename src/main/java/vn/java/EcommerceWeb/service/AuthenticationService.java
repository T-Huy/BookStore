package vn.java.EcommerceWeb.service;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import vn.java.EcommerceWeb.dto.request.ResetPasswordRequest;
import vn.java.EcommerceWeb.dto.request.SignInRequest;
import vn.java.EcommerceWeb.dto.response.TokenResponse;
import vn.java.EcommerceWeb.model.User;

public interface AuthenticationService {

    TokenResponse accessToken(SignInRequest signInRequest);
    TokenResponse refreshToken(HttpServletRequest request);
    String logout(HttpServletRequest request);
    String forgotPassword(String email) throws MessagingException;
    String resetPassword(String secretKey);
    String changePassword(ResetPasswordRequest request);
    User isValidUserByToken(String secretKey);
}
