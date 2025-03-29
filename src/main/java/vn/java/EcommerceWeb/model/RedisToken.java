package vn.java.EcommerceWeb.model;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("RedisToken")
public class RedisToken implements Serializable {

    @Id
    private String redisTokenId;

    private String accessToken;

    private String refreshToken;

    private String resetToken;
}
