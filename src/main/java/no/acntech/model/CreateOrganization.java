package no.acntech.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public record CreateOrganization(
        @NotBlank @Size(min = 2, max = 50) String name,
        @Size(max = 4000) String description,
        @NotNull @JsonProperty("is_private") Boolean isPrivate) {

    public record Request(@Valid @NotNull CreateOrganization organization) {
    }
}
