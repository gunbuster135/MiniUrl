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

public class UrlShortenerServiceTest {
    private Hasher hasher;
    private UrlRepository urlRepository;

    @Before
    public void setup() {
        hasher = Mockito.mock(Hasher.class);
        urlRepository = Mockito.mock(UrlRepository.class);

        Mockito.when(hasher.hash(any())).thenReturn("hash");
        Mockito.when(urlRepository.store(any()))
               .thenAnswer(invocation -> Mono.just(invocation.getArgument(0))); //echo back same response
    }

    @Test
    public void shouldCreateValidShortenedUrl() {
        final URI uri = URI.create("www.google.com");

        UrlShortenerService urlShortenerService = new UrlShortenerService(hasher, urlRepository);
        ShortenedUrl shortenedUrl = urlShortenerService.hashUrl(new UrlRequestBody(uri, Optional.empty()))
                                                       .block();

        assertThat(shortenedUrl, notNullValue());
        assertThat(shortenedUrl.getHashedUrl(), equalTo("hash"));
        assertThat(shortenedUrl.getOriginalUrl(), equalTo(uri));
        assertThat(shortenedUrl.getTtl(), equalTo(UrlShortenerService.DEFAULT_TTL));
    }


    @Test
    public void shouldCreateValidShortenedUrlWithProvidedTtl() {
        final URI uri = URI.create("www.google.com");
        final Long ttl = Duration.ofHours(5).toMillis();

        UrlShortenerService urlShortenerService = new UrlShortenerService(hasher, urlRepository);
        ShortenedUrl shortenedUrl = urlShortenerService.hashUrl(new UrlRequestBody(uri, Optional.of(ttl)))
                                                       .block();

        assertThat(shortenedUrl, notNullValue());
        assertThat(shortenedUrl.getHashedUrl(), equalTo("hash"));
        assertThat(shortenedUrl.getOriginalUrl(), equalTo(uri));
        assertThat(shortenedUrl.getTtl(), equalTo(Duration.ofMillis(ttl)));
    }
}