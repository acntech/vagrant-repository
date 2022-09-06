package no.acntech.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum VersionStatus {

    ACTIVE("active"),
    INACTIVE("inactive");

    private final String status;

    VersionStatus(String status) {
        this.status = status;
    }

    @JsonValue
    public String getStatus() {
        return status;
    }
}
