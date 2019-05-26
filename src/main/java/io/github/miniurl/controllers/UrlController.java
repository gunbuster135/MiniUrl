package io.github.miniurl.controllers;

import io.github.miniurl.api.UrlRequestBody;
import io.github.miniurl.api.UrlResponseBody;
import io.github.miniurl.urlshortener.UrlShortenerService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.net.URI;

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

    @GetMapping("/{hash}")
    public Mono<ResponseEntity<Void>> lookup(@NotBlank @PathVariable("hash") String hash) {
        return urlShortener.getUri(hash)
                           .flatMap(uri -> Mono.just(found(uri)))
                           .switchIfEmpty(Mono.just(new ResponseEntity<Void>(HttpStatus.NOT_FOUND)));
    }

    private ResponseEntity<Void> found(URI uri) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(uri);
        return new ResponseEntity<>(httpHeaders, HttpStatus.FOUND);
    }
}
