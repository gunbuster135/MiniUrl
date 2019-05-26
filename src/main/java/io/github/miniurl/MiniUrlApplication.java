package io.github.miniurl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;

@SpringBootApplication
@Configuration
public class MiniUrlApplication {

    public static void main(String[] args) {
        SpringApplication.run(MiniUrlApplication.class, args);
    }

    @Bean
    public ReactiveRedisTemplate<String, String> reactiveRedisTemplateString
            (ReactiveRedisConnectionFactory connectionFactory) {
        return new ReactiveRedisTemplate<>(connectionFactory, RedisSerializationContext.string());
    }

}
