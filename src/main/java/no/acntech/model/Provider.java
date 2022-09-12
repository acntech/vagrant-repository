package no.acntech.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.ZonedDateTime;

public record Provider(
        @NotNull @JsonIgnore Integer id,
        @NotNull ProviderType name,
        @NotNull Boolean hosted,
        @Size(max = 200) @JsonProperty("hosted_token") String hostedToken,
        @NotBlank @Size(max = 200) String checksum,
        @NotNull @JsonProperty("checksum_type") Algorithm checksumType,
        @NotNull @JsonIgnore ProviderStatus status,
        @NotNull @JsonIgnore Integer versionId,
        @Size(max = 200) @JsonProperty("original_url") String originalUrl,
        @Size(max = 200) @JsonProperty("download_url") String downloadUrl,
        @NotNull @JsonProperty("created_at") ZonedDateTime created,
        @JsonProperty("updated_at") ZonedDateTime modified) {

    public Provider with(@NotBlank @Size(max = 200) String downloadUrl) {
        return new Provider(
                this.id,
                this.name,
                this.hosted,
                this.hostedToken,
                this.checksum,
                this.checksumType,
                this.status,
                this.versionId,
                null,
                downloadUrl,
                this.created,
                this.modified);
    }
}
