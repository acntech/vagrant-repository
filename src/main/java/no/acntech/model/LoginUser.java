package no.acntech.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public record LoginUser(
        @NotBlank @Size(min = 2, max = 50) String login,
        @NotBlank @Size(min = 6, max = 50) String password) {
}
