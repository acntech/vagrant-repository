package no.acntech.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public record Storage(
        @NotBlank String uid,
        @NotNull Long fileSize,
        @NotBlank String checksum,
        @NotNull Algorithm checksumType) {
}
