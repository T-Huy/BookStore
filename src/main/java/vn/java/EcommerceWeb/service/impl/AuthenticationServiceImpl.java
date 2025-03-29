package vn.java.EcommerceWeb.service.impl;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.java.EcommerceWeb.dto.request.ResetPasswordRequest;
import vn.java.EcommerceWeb.dto.request.SignInRequest;
import vn.java.EcommerceWeb.dto.response.TokenResponse;
import vn.java.EcommerceWeb.enums.TokenType;
import vn.java.EcommerceWeb.exception.ResourceNotFoundException;
import vn.java.EcommerceWeb.model.RedisToken;
import vn.java.EcommerceWeb.model.Role;
import vn.java.EcommerceWeb.model.User;
import vn.java.EcommerceWeb.repository.RoleRepository;
import vn.java.EcommerceWeb.repository.UserRepository;
import vn.java.EcommerceWeb.service.*;

import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

//    private static final Logger log = LoggerFactory.getLogger(AuthenticationServiceImpl.class);
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final TokenService tokenService;
    private final UserService userService;
    private final MailService mailService;
    private final RedisTokenService redisTokenService;
    private final RoleRepository roleRepository;

    @Override
    public TokenResponse accessToken(SignInRequest signInRequest) {
        log.info("----------------------accessToken----------------------");

//        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signInRequest.getUsername(), signInRequest.getPassword()));
        var user = userRepository.findByEmail(signInRequest.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Email or password is incorrect"));

        if (!user.isEnabled()) {
            throw new ResourceNotFoundException("User is not active");
        }

        List<Role> listRoles = roleRepository.getAllRolesByUserId(user.getId());
        List<String> roles = listRoles.stream().map(Role::getName).toList();
        List<SimpleGrantedAuthority> authorities = roles.stream().map(SimpleGrantedAuthority::new).toList();

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signInRequest.getEmail(), signInRequest.getPassword(), authorities));

        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);


        //save token to redis
        redisTokenService.save(RedisToken.builder()
                .redisTokenId(user.getUsername())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build());

        //save token to db
//        tokenService.save(Token.builder()
//                .username(user.getUsername())
//                .accessToken(accessToken)
//                .refreshToken(refreshToken)
//                .build());

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .build();
    }

    @Override
    public TokenResponse refreshToken(HttpServletRequest request) {
        log.info("refresh token" + request.getHeader("x-token"));
        String refreshToken = request.getHeader("x-token");
        if (StringUtils.isBlank(refreshToken)) {
            throw new RuntimeException("Refresh Token must be not blank");
        }
        //extract username from token
        final String email = jwtService.extractUsername(refreshToken, TokenType.REFRESH_TOKEN);

        //check if token with database
        Optional<User> user = userRepository.findByEmail(email);

        if (!jwtService.isValid(refreshToken, TokenType.REFRESH_TOKEN, user.get())) {
            throw new RuntimeException("Refresh Token is invalid");
        }

        String accessToken = jwtService.generateToken(user.get());

        redisTokenService.save(RedisToken.builder()
                .redisTokenId(user.get().getUsername())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build());


        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.get().getId())
                .build();
    }

    @Override
    public String logout(HttpServletRequest request) {
        log.info("-------------------logout----------------------------");
        String accessToken = request.getHeader("Authorization");
        String token = StringUtils.substring(accessToken, "Bearer ".length());
        if (StringUtils.isBlank(token)) {
            throw new RuntimeException("AccessToken must be not blank");
        }

        //extract username from token
        final String email = jwtService.extractUsername(token, TokenType.ACCESS_TOKEN);

        redisTokenService.delete(email);

        //check if token with database
//        Token currentToken = tokenService.getByUsername(username);
//        tokenService.delete(currentToken);
        return "Delete logout access token success";
    }

    @Override
    public String forgotPassword(String email) throws MessagingException {
        log.info("----------------------forgot password service----------------------");
        //check email exist or not
        User user = userService.getByEmail(email);
        log.info("User: {}", user);
        //User is active or inactiveed
        if (!user.isEnabled()) {
            throw new RuntimeException("User is not active");
        }
        //generate reset token
        String resetPasswordToken = jwtService.generateResetPasswordToken(user);
        //save token to redis
        redisTokenService.save(RedisToken.builder()
                .redisTokenId(user.getUsername())
                .resetToken(resetPasswordToken)
                .build());
        //save to db
//        tokenService.save(Token.builder()
//                .username(user.getUsername())
//                .resetToken(resetPasswordToken)
//                .build());
        //TODO send email
        String confirmLink = String.format("curl --location 'http://localhost:80/v1/api/auth/confirm-reset-password' \\\n" + "--header 'Content-Type: application/json' \\\n" + "--data '%s'", resetPasswordToken);
        mailService.sendMail(email, "Reset Password", confirmLink, null);
        log.info("Send email forgot password success: {}", confirmLink);
        return "Send email forgot password success";
    }

    @Override
    public String resetPassword(String secretKey) {
        log.info("----------------------reset password----------------------");
        User user = isValidUserByToken(secretKey);
        //check token by username
//        tokenService.getByUsername(user.getUsername());
        redisTokenService.getById(user.getUsername());
        return "Reset password success";
    }

    @Override
    public String changePassword(ResetPasswordRequest request) {
        User user = isValidUserByToken(request.getSecretKey());
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Password and Confirm Password is not match");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
//        userService.saveUser(user);
        return "Change password success";
    }

    @Override
    public User isValidUserByToken(String secretKey) {
        final String email = jwtService.extractUsername(secretKey, TokenType.RESET_PASSWORD_TOKEN);
        User user = userService.getByEmail(email);
        if (!user.isEnabled()) {
            throw new RuntimeException("User is not active");
        }
        if (!jwtService.isValid(secretKey, TokenType.RESET_PASSWORD_TOKEN, user)) {
            throw new RuntimeException("Reset Password Token is invalid");
        }
        return user;
    }
}
