package no.acntech.common.model;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Valid
public class CreateProvider {

    @NotNull
    private ProviderType providerType;

    public ProviderType getProviderType() {
        return providerType;
    }
}
