package no.acntech.model;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public record UpdateOrganization(
        @Size(min = 2, max = 50) String name,
        @Size(max = 4000) String description) {

    public record Request(@Valid @NotNull UpdateOrganization organization) {
    }
}
