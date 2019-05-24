package io.github.miniurl.api;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.net.URI;

@Data
@AllArgsConstructor
public class UrlResponseBody {
    private final URI url;
}
