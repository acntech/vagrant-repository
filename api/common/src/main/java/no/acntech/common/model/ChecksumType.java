package no.acntech.common.model;

public enum ChecksumType {

    SHA1("sha1");

    private String name;

    ChecksumType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
