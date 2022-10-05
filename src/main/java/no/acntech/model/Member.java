package no.acntech.model;

import javax.validation.constraints.NotNull;

public record Member(
        @NotNull Integer id,
        @NotNull Integer organizationId,
        @NotNull Integer userId,
        @NotNull MemberRole role) {
}
