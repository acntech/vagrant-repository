package no.acntech.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public record TokenUser(@NotBlank @Size(min = 2, max = 50) String username) {
}
