package no.acntech.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import no.acntech.validation.CorrectVersion;

public class VersionForm {

    @NotBlank
    @CorrectVersion
    private String version;
    @Size(max = 4000)
    private String description;

    public VersionForm() {
    }

    public VersionForm(String version, String description) {
        this.version = version;
        this.description = description;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
