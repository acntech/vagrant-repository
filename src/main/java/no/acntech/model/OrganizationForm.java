package no.acntech.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class OrganizationForm {

    @NotBlank
    @Size(min = 2, max = 50)
    private String name;
    @Size(max = 4000)
    private String description;
    @NotNull
    private Boolean isPrivate;

    public OrganizationForm() {
    }

    public OrganizationForm(String name, String description, Boolean isPrivate) {
        this.name = name;
        this.description = description;
        this.isPrivate = isPrivate;
    }

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

    public Boolean getPrivate() {
        return isPrivate;
    }

    public void setPrivate(Boolean aPrivate) {
        isPrivate = aPrivate;
    }
}
