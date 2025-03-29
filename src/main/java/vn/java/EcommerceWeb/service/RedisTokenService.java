package vn.java.EcommerceWeb.service;

import vn.java.EcommerceWeb.model.RedisToken;

public interface RedisTokenService {
    String save(RedisToken redisToken);
    void delete(String id);
    RedisToken getById(String id);
}
