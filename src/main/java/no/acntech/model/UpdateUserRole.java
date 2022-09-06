package no.acntech.model;

import javax.validation.constraints.NotNull;

public record UpdateUserRole(@NotNull UserRole role) {
}
