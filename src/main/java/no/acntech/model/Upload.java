package no.acntech.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.ZonedDateTime;

public record Upload(
        @NotNull @JsonIgnore Integer id,
        @NotBlank @Size(max = 50) @JsonIgnore String uid,
        @NotBlank @Size(max = 200) @JsonProperty("upload_path") String uploadPath,
        @Size(max = 200) String callback,
        @JsonIgnore Long fileSize,
        @Size(max = 200) @JsonIgnore String checksum,
        @NotNull @JsonIgnore Algorithm checksumType,
        @NotNull @JsonIgnore Integer providerId,
        @NotNull @JsonIgnore ZonedDateTime created,
        @JsonIgnore ZonedDateTime modified) {
}
