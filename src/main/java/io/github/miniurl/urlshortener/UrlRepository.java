package io.github.miniurl.urlshortener;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import static org.hamcrest.core.IsNull.notNullValue;
import static org.valid4j.Assertive.require;

@Repository
public class UrlRepository {
    private final ReactiveRedisTemplate<String, String> redisTemplate;

    @Autowired
    public UrlRepository(ReactiveRedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = require(redisTemplate, notNullValue());
    }

    public Mono<Boolean> store(ShortenedUrl shortenedUrl) {
        return redisTemplate.opsForValue()
                            .set(shortenedUrl.getHashedUrl(), shortenedUrl.getOriginalUrl().toString(), shortenedUrl.getTtl());
    }
}
