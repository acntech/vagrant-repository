package no.acntech.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.ZonedDateTime;

public record Token(@Size(max = 200) String description,
                    @NotBlank @Size(max = 1000) String token,
                    @NotBlank @Size(max = 1000) @JsonProperty("token_hash") String tokenHash,
                    @NotNull @JsonProperty("created_at") ZonedDateTime created) {
}
