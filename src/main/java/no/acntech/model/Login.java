package no.acntech.model;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public record Login(LoginToken token, @Valid @NotNull LoginUser user) {
}
