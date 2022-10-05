package no.acntech.model;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import no.acntech.validation.CorrectVersion;

public record UpdateVersion(
        @NotBlank @CorrectVersion String version,
        @Size(max = 4000) String description) {

    public record Request(@Valid @NotNull UpdateVersion version) {
    }
}
