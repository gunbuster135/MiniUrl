package io.github.miniurl.controllers;

import io.github.miniurl.api.UrlRequestBody;
import io.github.miniurl.api.UrlResponseBody;
import io.github.miniurl.urlshortener.UrlShortenerService;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.net.URI;

import static org.hamcrest.core.IsNull.notNullValue;
import static org.valid4j.Assertive.require;

@RestController
public class UrlController {
    private final UrlShortenerService urlShortener;

    public UrlController(UrlShortenerService urlShortener) {
        this.urlShortener = require(urlShortener, notNullValue());
    }

    @PostMapping
    public Mono<UrlResponseBody> hashUrl(@RequestBody @Valid UrlRequestBody request,
                                         ServerHttpRequest serverRequest) {
        return urlShortener.hashUrl(request)
                           .map(shortenedUrl -> generateResponseUrl(shortenedUrl.getHashedUrl(), serverRequest))
                           .map(UrlResponseBody::new);
    }

    //Construct baseUri from request-uri. Spring should in most cases
    //be able to infer from the http request the proper request URI
    private URI generateResponseUrl(String pathValue, ServerHttpRequest serverRequest) {
        URI requestUri = serverRequest.getURI();

        return UriComponentsBuilder.fromUri(requestUri)
                                   .replacePath(pathValue)
                                   .replaceQuery(null)
                                   .build()
                                   .toUri();
    }
}
