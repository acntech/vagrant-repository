package no.acntech.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

public record Token(String description,
                    @NotBlank String token,
                    @NotBlank @JsonProperty("token_hash") String tokenHash,
                    @NotNull @JsonProperty("created_at") ZonedDateTime created) {
}
