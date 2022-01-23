package no.acntech.model;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Valid
public class ModifyProvider {

    @NotNull
    private ProviderType providerType;

    public ProviderType getProviderType() {
        return providerType;
    }
}
