package no.acntech.model;

import javax.validation.constraints.NotNull;

public record OrganizationMember(
        @NotNull Integer id,
        @NotNull Integer organizationId,
        @NotNull Integer userId,
        @NotNull OrganizationRole role) {
}
