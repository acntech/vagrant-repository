package no.acntech.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import javax.validation.constraints.NotBlank;

public enum ProviderType {

    VIRTUALBOX("virtualbox");

    private final String provider;

    ProviderType(String provider) {
        this.provider = provider;
    }

    @JsonValue
    public String getProvider() {
        return provider;
    }

    @JsonCreator
    public static ProviderType fromProvider(@NotBlank final String provider) {
        for (ProviderType providerType : ProviderType.values()) {
            if (providerType.provider.equals(provider)) {
                return providerType;
            }
        }
        throw new IllegalArgumentException("Value is not a valid provider");
    }
}
