package no.acntech.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class VersionForm {

    @NotBlank
    @Size(min = 2, max = 50)
    private String name;
    @Size(max = 4000)
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
