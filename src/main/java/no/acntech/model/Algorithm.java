package no.acntech.model;

public enum Algorithm {

    SHA256("sha256"),
    SHA512("sha512");

    private final String name;

    Algorithm(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
