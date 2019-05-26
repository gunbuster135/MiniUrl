package io.github.miniurl.it;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.net.URI;

import static java.lang.String.format;
import static org.hamcrest.core.IsEqual.equalTo;

@AutoConfigureWebTestClient
public class MiniUrlFetchIT extends BaseIT {
    @Autowired
    private WebTestClient webClient;

    @Autowired
    private ReactiveRedisTemplate<String, String> redisTemplate;

    @Test
    public void shouldFetchOriginalUrl() {
        final URI uri = URI.create("www.google.com");
        final String hash = "abc123";
        //setup state in redis
        redisTemplate.opsForValue()
                     .set(hash, uri.toString())
                     .block();

        webClient.get()
                 .uri(format("/%s", hash))
                 .exchange()
                 .expectStatus().isFound()
                 .expectHeader().value(HttpHeaders.LOCATION, equalTo(uri.toString()));
    }

    @Test
    public void shouldFailFetchingOriginalUrl() {
        webClient.get()
                 .uri("/does-not-exist")
                 .exchange()
                 .expectStatus().isNotFound();
    }
}
