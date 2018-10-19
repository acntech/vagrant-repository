package no.acntech.common.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Table(name = "GROUPS")
@Entity
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Column(unique = true)
    private String name;
    private String description;
    @OneToMany(mappedBy = "group", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Box> boxes;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<Box> getBoxes() {
        return boxes;
    }

    @JsonIgnore
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String name;
        private String description;
        private List<Box> boxes;

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

        public Builder boxes(List<Box> boxes) {
            this.boxes = boxes;
            return this;
        }

        public Group build() {
            Group group = new Group();
            group.boxes = this.boxes;
            group.name = this.name;
            group.description = this.description;
            return group;
        }
    }
}
