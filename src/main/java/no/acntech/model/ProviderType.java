package no.acntech.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.util.Assert;

public enum ProviderType {

    HYPER_V("hyper-v"),
    VIRTUALBOX("virtualbox"),
    VMWARE("vmware");

    private final String provider;

    ProviderType(String provider) {
        this.provider = provider;
    }

    @JsonValue
    @Override
    public String toString() {
        return provider;
    }

    @JsonCreator
    public static ProviderType fromProvider(final String provider) {
        Assert.hasText(provider, "Provider is null");
        for (ProviderType providerType : ProviderType.values()) {
            if (providerType.provider.equals(provider)) {
                return providerType;
            }
        }
        throw new IllegalArgumentException("\"" + provider + "\" is not a supported provider");
    }
}
