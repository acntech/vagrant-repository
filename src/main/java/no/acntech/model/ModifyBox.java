package no.acntech.model;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@Valid
public class ModifyBox {

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
