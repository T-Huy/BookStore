package vn.java.EcommerceWeb.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.java.EcommerceWeb.service.RedisService;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/v1/api/redis")
@RequiredArgsConstructor
public class RedisController {

    private static final Logger log = LoggerFactory.getLogger(RedisController.class);
    private final RedisService redisService;
    private final RedisTemplate<String, Object> redisTemplate;

    @GetMapping("/keys")
    public Set<String> getKeys() {
        return redisService.getAllKeys();
    }

    @GetMapping("/get")
    public Object getValue(@RequestParam String key) {
        return redisService.getValueByKey(key);
    }

    @GetMapping("/hash/{key}")
    public ResponseEntity<Map<Object, Object>> getHash(@PathVariable String key) {
        log.info("Fetching hash for key: {}", key);
        Map<Object, Object> hashValues = redisTemplate.opsForHash().entries(key);
        log.info("Hash values: {}", hashValues);
        return ResponseEntity.ok(hashValues);
    }

}
