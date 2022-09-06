package no.acntech.model;

import javax.validation.constraints.NotNull;
import java.util.List;

public record Error(
        @NotNull List<String> errors,
        @NotNull Boolean message) {
}
