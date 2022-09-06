package no.acntech.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.ZonedDateTime;
import java.util.List;

public record Version(
        @NotNull @JsonIgnore Integer id,
        @NotBlank @Size(min = 1, max = 10) String version,
        @NotBlank @Size(min = 1, max = 10) String number,
        @Size(max = 4000) @JsonProperty("description_html") String descriptionHtml,
        @Size(max = 4000) @JsonProperty("description_markdown") String descriptionMarkdown,
        @NotNull VersionStatus status,
        @Size(max = 200) @JsonProperty("release_url") String releaseUrl,
        @Size(max = 200) @JsonProperty("revoke_url") String revokeUrl,
        @NotNull @JsonProperty("created_at") ZonedDateTime created,
        @JsonProperty("updated_at") ZonedDateTime modified,
        @Valid List<Provider> providers) {
}
