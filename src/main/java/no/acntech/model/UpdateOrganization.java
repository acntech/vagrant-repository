package no.acntech.model;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;

public record UpdateOrganization(
        @NotBlank @Max(4000) String description) {
}
