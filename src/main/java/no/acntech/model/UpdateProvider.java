package no.acntech.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public record UpdateProvider(
        @NotNull ProviderType name,
        @NotBlank @Size(max = 200) String checksum,
        @NotNull @JsonProperty("checksum_type") Algorithm checksumType,
        @Size(max = 200) String url) {

    public record Request(@Valid @NotNull UpdateProvider provider) {
    }
}
