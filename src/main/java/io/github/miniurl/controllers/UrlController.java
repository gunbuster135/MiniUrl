package io.github.miniurl.controllers;

import io.github.miniurl.api.UrlRequestBody;
import io.github.miniurl.api.UrlResponseBody;
import io.github.miniurl.urlshortener.UrlShortenerService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

import static org.hamcrest.core.IsNull.notNullValue;
import static org.valid4j.Assertive.require;

@RestController
public class UrlController {
    private final UrlShortenerService urlShortener;

    public UrlController(UrlShortenerService urlShortener) {
        this.urlShortener = require(urlShortener, notNullValue());
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Mono<UrlResponseBody> hashUrl(@RequestBody @Valid UrlRequestBody request) {
        return urlShortener.hashUrl(request)
                           .map(shortenedUrl -> new UrlResponseBody(shortenedUrl.getHashedUrl()));
    }
}
