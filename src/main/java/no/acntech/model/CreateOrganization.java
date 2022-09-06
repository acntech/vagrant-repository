package no.acntech.model;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

public record CreateOrganization(
        @NotBlank @Min(2) @Max(50) String name,
        @Max(4000) String description) {
}
