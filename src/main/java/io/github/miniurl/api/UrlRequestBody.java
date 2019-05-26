package io.github.miniurl.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.net.URI;
import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UrlRequestBody {
    @NotNull
    private URI url;

    @Positive
    private Long ttl;

    public Optional<Long> getTtl() {
        return Optional.ofNullable(ttl);
    }
}
