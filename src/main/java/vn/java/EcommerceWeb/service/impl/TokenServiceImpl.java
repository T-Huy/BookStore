package vn.java.EcommerceWeb.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import vn.java.EcommerceWeb.model.Token;
import vn.java.EcommerceWeb.repository.RoleRepository;
import vn.java.EcommerceWeb.repository.TokenRepository;
import vn.java.EcommerceWeb.service.TokenService;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final TokenRepository tokenRepository;
    @Override
    public Long save(Token token) {
        Optional<Token> optional = tokenRepository.findByEmail(token.getEmail());
        if (optional.isEmpty()) {
            tokenRepository.save(token);
            return token.getId();
        } else {
            Token currentToken = optional.get();
            currentToken.setAccessToken(token.getAccessToken());
            currentToken.setRefreshToken(token.getRefreshToken());
            tokenRepository.save(currentToken);
            return currentToken.getId();
        }
    }

    @Override
    public String delete(Token token) {
        tokenRepository.delete(token);
        return "Token deleted";
    }

    @Override
    public Token getByEmail(String email) {
        return tokenRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Token not found"));
    }
}
