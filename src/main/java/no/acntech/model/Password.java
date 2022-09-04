package no.acntech.model;

import javax.validation.constraints.NotBlank;

public record Password(@NotBlank String passwordHash,
                       @NotBlank String passwordSalt) {
}
