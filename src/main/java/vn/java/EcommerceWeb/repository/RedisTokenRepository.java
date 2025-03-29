package vn.java.EcommerceWeb.repository;

import org.springframework.data.repository.CrudRepository;
import vn.java.EcommerceWeb.model.RedisToken;

public interface RedisTokenRepository extends CrudRepository<RedisToken, String> {
    RedisToken findByAccessToken(String accessToken);

    RedisToken findByRefreshToken(String refreshToken);

    RedisToken findByResetToken(String resetToken);
}