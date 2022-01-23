package no.acntech.model;

import java.util.stream.Stream;

public enum ProviderType {

    VIRTUALBOX("virtualbox", "VirtualBox");

    private String name;
    private String readable;

    ProviderType(String name, String readable) {
        this.name = name;
        this.readable = readable;
    }

    public String getName() {
        return name;
    }

    public String getReadable() {
        return readable;
    }

    public static ProviderType fromName(String name) {
        return Stream.of(values())
                .filter(providerType -> providerType.name.equals(name))
                .findFirst()
                .orElse(null);
    }
}
