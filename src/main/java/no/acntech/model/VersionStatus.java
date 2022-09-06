package no.acntech.model;

public enum VersionStatus {
    ACTIVE("active"), INACTIVE("inactive");

    private final String readable;

    VersionStatus(String readable) {
        this.readable = readable;
    }

    public String getReadable() {
        return readable;
    }
}
