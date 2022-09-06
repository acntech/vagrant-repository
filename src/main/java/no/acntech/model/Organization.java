package no.acntech.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

public record Organization(
        @NotNull @JsonIgnore Integer id,
        @NotBlank @Min(2) @Max(50) String name,
        @NotBlank @Max(4000) String description,
        @NotNull ZonedDateTime created,
        ZonedDateTime modified) {
}
