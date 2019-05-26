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
    //ttl is enforced, so we'll need to define a default TTL
    public final static Duration DEFAULT_TTL = Duration.ofDays(365 * 2); //approx...
    private final Hasher hasher;
    private final UrlRepository urlRepository;

    @Autowired
    public UrlShortenerService(Hasher hasher, UrlRepository urlRepository) {
        this.hasher = require(hasher, notNullValue());
        this.urlRepository = require(urlRepository, notNullValue());
    }

    /**
     * Hashes & stores a URL wrapped in a UrlRequestBody. Will normalize the URL
     * to reduce "duplicate" urls that may differ but point to same location.
     * TTL is optional but will use a default TTL of ~2 years if not provided.
     *
     * @param urlRequestBody Request body of a url to be hashed
     * @return A domain object representing the hashed url
     */
    public Mono<ShortenedUrl> hashUrl(UrlRequestBody urlRequestBody) {
        URI normalizedUrl = urlRequestBody.getUrl()
                                          .normalize();
        String hashedUrl = hasher.hash(normalizedUrl);

        Duration ttl = urlRequestBody.getTtl()
                                     .map(Duration::ofMillis)
                                     .orElse(DEFAULT_TTL);

        return urlRepository.store(new ShortenedUrl(normalizedUrl, hashedUrl, ttl))
                            .log()
                            .onErrorMap(throwable -> new UrlShortenerException("Failed to store hashed URL", throwable));
    }

    /**
     * Returns a URL by providing a hash, triggering a lookup in the persistent data store
     *
     * @param hash Hash generated when calling hashUrl()
     * @return A mono that may or may not contain an URI
     */
    public Mono<URI> getUri(String hash) {
        return Mono.just(hash)
                   .log()
                   .flatMap(urlRepository::fetch);
    }
}
