package io.github.miniurl.it;

import io.github.miniurl.api.UrlRequestBody;
import io.github.miniurl.api.UrlResponseBody;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;

@AutoConfigureWebTestClient
public class MiniUrlCreateIT extends BaseIT {

    @Autowired
    private WebTestClient webClient;

    @Autowired
    private ReactiveRedisTemplate<String, String> redisTemplate;

    @Test
    public void shouldCreateHashedUrl() {
        final URI uri = URI.create("www.google.com");
        final UrlRequestBody request = new UrlRequestBody(uri, Optional.of(15_000L));

        UrlResponseBody result = webClient.post()
                                          .uri("/")
                                          .body(BodyInserters.fromObject(request))
                                          .exchange()
                                          .expectStatus().isOk()
                                          .returnResult(UrlResponseBody.class)
                                          .getResponseBody()
                                          .blockFirst();

        //Verify response
        assertThat(result, notNullValue());
        assertThat(result.getUrl(), notNullValue());

        //Verify state in redis
        URI actualOriginalUri = redisTemplate.opsForValue()
                                             .get(result.getUrl().getPath())
                                             .map(URI::create)
                                             .block();
        assertThat(actualOriginalUri, equalTo(uri));
    }
}
