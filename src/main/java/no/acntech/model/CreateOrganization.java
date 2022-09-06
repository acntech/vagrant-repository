package no.acntech.model;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public record CreateOrganization(
        @NotBlank @Size(min = 2, max = 50) String name,
        @Size(max = 4000) String description) {

    public record Request(@Valid @NotNull CreateOrganization organization) {
    }
}
