package no.acntech.common.model;

public enum ProviderType {

    VIRTUALBOX("VirtualBox");

    private String readable;

    ProviderType(String readable) {
        this.readable = readable;
    }

    public String getReadable() {
        return readable;
    }
}
