package no.acntech.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Algorithm {

    SHA256("sha256"),
    SHA512("sha512");

    private final String algorithm;

    Algorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    @JsonValue
    @Override
    public String toString() {
        return algorithm;
    }
}
