package no.acntech.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public record Password(@NotBlank @Size(max = 100) String passwordHash,
                       @NotBlank @Size(max = 100) String passwordSalt) {
}
