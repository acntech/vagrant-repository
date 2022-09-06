package no.acntech.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

public record User(
        @NotNull @JsonIgnore Integer id,
        @NotBlank @Min(2) @Max(50) String username,
        @NotBlank UserRole role,
        @NotBlank @JsonIgnore String passwordHash,
        @NotBlank @JsonIgnore String passwordSalt,
        @NotNull ZonedDateTime created,
        ZonedDateTime modified) {
}