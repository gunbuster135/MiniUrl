package io.github.miniurl.urlshortener;

import io.github.miniurl.api.UrlRequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Duration;

import static org.hamcrest.core.IsNull.notNullValue;
import static org.valid4j.Assertive.require;

@Service
public class UrlShortenerService {
    private final static Duration DEFAULT_EXPIRATION = Duration.ofDays(365 * 2); //approx...
    private final Hasher hasher;
    private final UrlRepository urlRepository;

    @Autowired
    public UrlShortenerService(Hasher hasher, UrlRepository urlRepository) {
        this.hasher = require(hasher, notNullValue());
        this.urlRepository = require(urlRepository, notNullValue());
    }

    public Mono<ShortenedUrl> hashUrl(UrlRequestBody urlRequestBody) {
        URI normalizedUrl = urlRequestBody.getUrl()
                                          .normalize();
        String hashedUrl = hasher.hash(normalizedUrl);
        Duration ttl = urlRequestBody.getTtl()
                                     .map(Duration::ofMillis)
                                     .orElse(DEFAULT_EXPIRATION);

        return urlRepository.store(new ShortenedUrl(normalizedUrl, hashedUrl, ttl))
                            .log()
                            .onErrorMap(throwable -> new UrlShortenerException("Failed to store hashed URL", throwable));
    }
}
