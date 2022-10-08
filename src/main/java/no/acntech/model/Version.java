package no.acntech.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.ZonedDateTime;
import java.util.List;

import no.acntech.validation.CorrectVersion;

public record Version(
        @NotNull @JsonIgnore Integer id,
        @NotBlank @CorrectVersion String version,
        @NotBlank @CorrectVersion String number,
        @Size(max = 4000) @JsonProperty("description_html") String descriptionHtml,
        @Size(max = 4000) @JsonProperty("description_markdown") String descriptionMarkdown,
        @NotNull VersionStatus status,
        @NotNull @JsonIgnore Integer boxId,
        @Size(max = 200) @JsonProperty("release_url") String releaseUrl,
        @Size(max = 200) @JsonProperty("revoke_url") String revokeUrl,
        @NotNull @JsonProperty("created_at") ZonedDateTime created,
        @JsonProperty("updated_at") ZonedDateTime modified,
        @Valid List<Provider> providers) {

    public Version with(@NotBlank @Size(max = 200) String releaseUrl,
                        @NotBlank @Size(max = 200) String revokeUrl,
                        @Valid @NotNull List<Provider> providers) {
        return new Version(
                this.id,
                this.version,
                this.number,
                this.descriptionHtml,
                this.descriptionMarkdown,
                this.status,
                this.boxId,
                releaseUrl,
                revokeUrl,
                this.created,
                this.modified,
                providers);
    }
}
