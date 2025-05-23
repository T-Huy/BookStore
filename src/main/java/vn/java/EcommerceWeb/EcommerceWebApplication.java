package vn.java.EcommerceWeb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableRedisRepositories
@EnableScheduling
public class EcommerceWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(EcommerceWebApplication.class, args);
	}

}
