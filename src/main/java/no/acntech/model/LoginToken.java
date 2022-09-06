package no.acntech.model;

import javax.validation.constraints.Size;

public record LoginToken(
        @Size(max = 4000) String description) {
}
