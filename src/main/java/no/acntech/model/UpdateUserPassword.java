package no.acntech.model;

import javax.validation.constraints.NotBlank;

public record UpdateUserPassword(@NotBlank String username,
                                 @NotBlank String password) {
}
