package no.acntech.model;

import javax.validation.constraints.NotBlank;
import java.time.ZonedDateTime;

public class Box {

    private Long id;
    @NotBlank
    private String name;
    private String description;
    private ZonedDateTime created;
    private Group group;

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

    public Group getGroup() {
        return group;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String name;
        private String description;
        private Group group;

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

        public Builder group(Group group) {
            this.group = group;
            return this;
        }

        public Box build() {
            var box = new Box();
            box.name = this.name;
            box.description = this.description;
            box.group = this.group;
            return box;
        }
    }
}
