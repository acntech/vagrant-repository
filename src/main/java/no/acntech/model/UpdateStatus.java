package no.acntech.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum UpdateStatus {

    ACTIVE("active"),
    INACTIVE("inactive");

    private final String status;

    UpdateStatus(String status) {
        this.status = status;
    }

    @JsonValue
    @Override
    public String toString() {
        return status;
    }
}
