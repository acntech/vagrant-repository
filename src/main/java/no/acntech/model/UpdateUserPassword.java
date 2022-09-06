package no.acntech.model;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

public record UpdateUserPassword(@NotBlank @Min(6) @Max(50) String oldPassword,
                                 @NotBlank @Min(6) @Max(50) String newPassword) {
}
