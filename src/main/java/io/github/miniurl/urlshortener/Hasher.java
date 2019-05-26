package io.github.miniurl.urlshortener;

import com.google.common.hash.Hashing;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.core.IsNull.notNullValue;
import static org.valid4j.Assertive.require;

public interface Hasher {

    String hash(URI url);

    @SuppressWarnings("UnstableApiUsage")
    @Component
    class Murmur32Hasher implements Hasher {
        private final static Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

        @Override
        public String hash(URI url) {
            require(url, notNullValue());

            return Hashing.murmur3_32()
                          .hashString(url.toString(), DEFAULT_CHARSET)
                          .toString();
        }
    }
}
