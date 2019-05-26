package io.github.miniurl.urlshortener;

import io.github.miniurl.api.UrlRequestBody;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Duration;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

public class UrlShortenerServiceTest {
    private final static URI DEFAULT_URI = URI.create("www.google.com");
    private Hasher hasher;
    private UrlRepository urlRepository;

    @Before
    public void setup() {
        hasher = Mockito.mock(Hasher.class);
        urlRepository = Mockito.mock(UrlRepository.class);

        Mockito.when(hasher.hash(any())).thenReturn("hash");
        Mockito.when(urlRepository.store(any()))
               .thenAnswer(invocation -> Mono.just(invocation.getArgument(0))); //echo back same response
        Mockito.when(urlRepository.fetch(eq("hash")))
               .thenReturn(Mono.just(URI.create("www.google.com")));
    }

    @Test
    public void shouldCreateValidShortenedUrl() {

        UrlShortenerService urlShortenerService = new UrlShortenerService(hasher, urlRepository);
        ShortenedUrl shortenedUrl = urlShortenerService.hashUrl(new UrlRequestBody(DEFAULT_URI, null))
                                                       .block();

        assertThat(shortenedUrl, notNullValue());
        assertThat(shortenedUrl.getHashedUrl(), equalTo("hash"));
        assertThat(shortenedUrl.getOriginalUrl(), equalTo(DEFAULT_URI));
        assertThat(shortenedUrl.getTtl(), equalTo(UrlShortenerService.DEFAULT_TTL));
    }

    @Test
    public void shouldCreateValidShortenedUrlWithProvidedTtl() {
        final Long ttl = Duration.ofHours(5).toMillis();

        UrlShortenerService urlShortenerService = new UrlShortenerService(hasher, urlRepository);
        ShortenedUrl shortenedUrl = urlShortenerService.hashUrl(new UrlRequestBody(DEFAULT_URI, ttl))
                                                       .block();

        assertThat(shortenedUrl, notNullValue());
        assertThat(shortenedUrl.getHashedUrl(), equalTo("hash"));
        assertThat(shortenedUrl.getOriginalUrl(), equalTo(DEFAULT_URI));
        assertThat(shortenedUrl.getTtl(), equalTo(Duration.ofMillis(ttl)));
    }

    @Test
    public void shouldGetValidUrl() {
        UrlShortenerService urlShortenerService = new UrlShortenerService(hasher, urlRepository);
        URI result = urlShortenerService.getUri("hash")
                                        .block();

        assertThat(result, equalTo(DEFAULT_URI));
    }
}