package no.acntech.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class VersionForm {

    @NotBlank
    @Size(min = 2, max = 50)
    private String version;
    @Size(max = 4000)
    private String description;

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
