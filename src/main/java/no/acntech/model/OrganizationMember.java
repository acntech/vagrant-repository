package no.acntech.model;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public record OrganizationMember(
        @NotBlank @Min(2) @Max(50) String username,
        @NotNull OrganizationRole role) {
}
