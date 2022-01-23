package no.acntech.model;

public enum ChecksumType {

    SHA1("sha1"),
    SHA256("sha256"),
    SHA512("sha512");

    private final String name;

    ChecksumType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
