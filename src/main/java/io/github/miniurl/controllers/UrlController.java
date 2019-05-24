package io.github.miniurl.controllers;

import io.github.miniurl.api.UrlRequestBody;
import io.github.miniurl.api.UrlResponseBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.net.URI;

@RestController
public class UrlController {

    @PostMapping
    public Mono<UrlResponseBody> hashUrl(@RequestBody @Valid UrlRequestBody request) {
        return Mono.just(new UrlResponseBody(URI.create("localhost.com")));
    }
}
