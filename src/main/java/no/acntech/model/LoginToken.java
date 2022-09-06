package no.acntech.model;

import javax.validation.constraints.Size;

public record LoginToken(
        @Size(max = 200) String description) {
}
