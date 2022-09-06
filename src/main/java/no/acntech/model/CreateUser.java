package no.acntech.model;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

public record CreateUser(
        @NotBlank @Min(2) @Max(50) String username,
        @NotBlank @Min(6) @Max(50) String password) {
}
