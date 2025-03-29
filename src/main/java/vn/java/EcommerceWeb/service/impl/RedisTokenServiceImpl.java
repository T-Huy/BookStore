package vn.java.EcommerceWeb.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.java.EcommerceWeb.exception.ResourceNotFoundException;
import vn.java.EcommerceWeb.model.RedisToken;
import vn.java.EcommerceWeb.repository.RedisTokenRepository;
import vn.java.EcommerceWeb.service.RedisTokenService;

@Service
@RequiredArgsConstructor
public class RedisTokenServiceImpl implements RedisTokenService {

    private final RedisTokenRepository redisTokenRepository;

    @Override
    public String save(RedisToken redisToken) {
        RedisToken result = redisTokenRepository.save(redisToken);
        return result.getRedisTokenId();
    }

    @Override
    public void delete(String id) {
        redisTokenRepository.deleteById(id);
    }

    @Override
    public RedisToken getById(String id) {
        return redisTokenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RedisToken not found"));
    }
}
