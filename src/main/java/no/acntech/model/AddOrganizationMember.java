package no.acntech.model;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public record AddOrganizationMember(
        @NotBlank @Size(min = 2, max = 50) String username,
        @NotNull OrganizationRole role) {

    public record Request(@Valid @NotNull AddOrganizationMember organization) {
    }
}
