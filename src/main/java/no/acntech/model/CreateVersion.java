package no.acntech.model;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public record CreateVersion(
        @NotBlank @Size(min = 1, max = 10) String name,
        @Size(max = 4000) String description) {

    public record Request(@Valid @NotNull CreateVersion version) {
    }
}
