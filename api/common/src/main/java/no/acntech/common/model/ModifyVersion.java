package no.acntech.common.model;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@Valid
public class ModifyVersion {

    @NotBlank
    private String name;
    private String description;

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
