package io.github.miniurl.it;

import io.github.miniurl.Environment;
import io.github.miniurl.api.UrlRequestBody;
import io.github.miniurl.api.UrlResponseBody;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.testcontainers.containers.GenericContainer;

import java.net.URI;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureWebTestClient
public class MiniUrlCreateIT {
    @ClassRule
    public static GenericContainer redis = new GenericContainer("redis:5.0.5").withExposedPorts(Environment.REDIS_PORT);

    @Autowired
    private WebTestClient webClient;

    @Test
    public void shouldCreateHashedUrl() {
        final UrlRequestBody request = new UrlRequestBody(URI.create("www.google.com"), Optional.of(15_000L));

        UrlResponseBody result = webClient.post()
                                          .uri("/")
                                          .body(BodyInserters.fromObject(request))
                                          .exchange()
                                          .expectStatus().isOk()
                                          .returnResult(UrlResponseBody.class)
                                          .getResponseBody()
                                          .blockFirst();

        assertThat(result, notNullValue());
        assertThat(result.getUrl(), notNullValue());

        //todo: assert redis state
    }
}
