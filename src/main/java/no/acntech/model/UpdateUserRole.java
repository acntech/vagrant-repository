package no.acntech.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public record UpdateUserRole(@NotBlank String username,
                             @NotNull Role role) {
}
