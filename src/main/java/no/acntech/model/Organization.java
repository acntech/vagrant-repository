package no.acntech.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.ZonedDateTime;

public record Organization(
        @NotNull @JsonIgnore Integer id,
        @NotBlank @Size(min = 2, max = 50) String name,
        @NotBlank @Size(max = 4000) String description,
        @NotNull @JsonProperty("private") Boolean isPrivate,
        @NotNull @JsonProperty("created_at") ZonedDateTime created,
        @JsonProperty("updated_at") ZonedDateTime modified) {
}
