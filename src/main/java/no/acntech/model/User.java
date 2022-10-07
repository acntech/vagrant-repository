package no.acntech.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.ZonedDateTime;

public record User(
        @NotNull @JsonIgnore Integer id,
        @NotBlank @Size(min = 2, max = 50) String username,
        @NotNull UserRole role,
        @NotBlank @Size(max = 100) @JsonIgnore String passwordHash,
        @NotNull @JsonProperty("created_at") ZonedDateTime created,
        @JsonProperty("updated_at") ZonedDateTime modified) {
}
