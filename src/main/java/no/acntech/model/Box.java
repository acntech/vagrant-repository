package no.acntech.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.ZonedDateTime;
import java.util.List;

public record Box(
        @NotNull @JsonIgnore Integer id,
        @NotBlank @Size(min = 2, max = 101) String tag,
        @NotBlank @Size(min = 2, max = 50) String name,
        @NotBlank @Size(min = 2, max = 50) String username,
        @Size(max = 4000) @JsonProperty("short_description") String descriptionShort,
        @Size(max = 4000) @JsonProperty("description_html") String descriptionHtml,
        @Size(max = 4000) @JsonProperty("description_markdown") String descriptionMarkdown,
        @NotNull @JsonProperty("private") Boolean isPrivate,
        @NotNull Integer downloads,
        @NotNull @JsonIgnore Integer organizationId,
        @NotNull @JsonProperty("created_at") ZonedDateTime created,
        @JsonProperty("updated_at") ZonedDateTime modified,
        @Valid @JsonProperty("current_version") Version currentVersion,
        @Valid List<Version> versions) {

    public Box with(Version currentVersion, @Valid @NotNull List<Version> versions) {
        return new Box(
                this.id,
                this.tag,
                this.name,
                this.username,
                this.descriptionShort,
                this.descriptionHtml,
                this.descriptionMarkdown,
                this.isPrivate,
                this.downloads,
                this.organizationId,
                this.created,
                this.modified,
                currentVersion,
                versions);
    }
}
