package no.acntech.model;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public record CreateUser(
        @NotBlank @Size(min = 2, max = 50) String username,
        @NotBlank @Size(min = 6, max = 50) String password,
        @NotNull UserRole role) {

    public record Request(@Valid @NotNull CreateUser user) {
    }
}
