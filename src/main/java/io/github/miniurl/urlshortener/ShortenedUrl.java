package io.github.miniurl.urlshortener;

import lombok.Data;

import java.net.URI;
import java.time.Duration;

import static org.hamcrest.core.IsNull.notNullValue;
import static org.valid4j.Assertive.ensure;
import static org.valid4j.Assertive.require;
import static org.valid4j.matchers.ArgumentMatchers.notEmptyString;

@Data
public class ShortenedUrl {
    private final URI originalUrl;
    private final String hashedUrl;
    private final Duration ttl;

    //Proper domain validation when instantiating
    public ShortenedUrl(URI originalUrl,
                        String hashedUrl,
                        Duration ttl) {
        this.originalUrl = require(originalUrl, notNullValue());
        this.hashedUrl = require(hashedUrl, notEmptyString());
        this.ttl = require(ttl, notNullValue());
        ensure(!(ttl.isZero() && ttl.isNegative())); //Ensure ttl is positive
    }
}
