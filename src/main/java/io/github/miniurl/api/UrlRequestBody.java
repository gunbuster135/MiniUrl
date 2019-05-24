package io.github.miniurl.api;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.net.URI;
import java.util.Optional;

@Data
@NoArgsConstructor
public class UrlRequestBody {
    @NotNull
    private URI url;

    private Optional<@Positive Long> ttl;
}
