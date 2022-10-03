package no.acntech.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SearchProviderType {

    HYPER_V("hyper-v"),
    VIRTUALBOX("virtualbox"),
    VMWARE("vmware");

    private final String provider;

    SearchProviderType(String provider) {
        this.provider = provider;
    }

    @JsonValue
    @Override
    public String toString() {
        return provider;
    }

    @JsonCreator
    public static SearchProviderType fromProvider(final String provider) {
        for (SearchProviderType providerType : SearchProviderType.values()) {
            if (providerType.provider.equals(provider)) {
                return providerType;
            }
        }
        return null;
    }
}
