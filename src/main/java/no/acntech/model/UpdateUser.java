package no.acntech.model;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public record UpdateUser(@Size(min = 2, max = 50) String username,
                         @Size(min = 6, max = 50) String oldPassword,
                         @Size(min = 6, max = 50) String newPassword,
                         UserRole role) {

    public record Request(@Valid @NotNull UpdateUser user) {
    }
}
