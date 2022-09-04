package no.acntech.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.validation.constraints.NotBlank;
import java.time.ZonedDateTime;

public class Version {

    private Long id;
    @NotBlank
    private String name;
    private String description;
    private ZonedDateTime created;
    private ZonedDateTime modified;
    private Box box;

    public Long getId() {
        return id;
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

    public ZonedDateTime getCreated() {
        return created;
    }

    public ZonedDateTime getModified() {
        return modified;
    }

    public Box getBox() {
        return box;
    }

    @JsonIgnore
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String name;
        private String description;
        private Box box;

        private Builder() {
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder box(Box box) {
            this.box = box;
            return this;
        }

        public Version build() {
            Version version = new Version();
            version.description = this.description;
            version.name = this.name;
            version.box = this.box;
            return version;
        }
    }
}
