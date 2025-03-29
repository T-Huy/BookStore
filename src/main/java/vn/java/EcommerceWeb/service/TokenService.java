package vn.java.EcommerceWeb.service;

import vn.java.EcommerceWeb.model.Token;

public interface TokenService {
    Long save(Token token);

    String delete(Token token);

    Token getByEmail(String email);
}
