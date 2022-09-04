package no.acntech.model;

import javax.validation.constraints.NotBlank;

public record LoginUser(@NotBlank String login,
                        @NotBlank String password) {
}
