package no.acntech.common.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Table(name = "BOXES")
@Entity
public class Box {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Column(unique = true)
    private String name;
    private String description;
    @ManyToOne
    @JoinColumn(name = "GROUP_ID", nullable = false)
    private Group group;
    @OneToMany(mappedBy = "box", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Version> versions;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Group getGroup() {
        return group;
    }

    public List<Version> getVersions() {
        return versions;
    }

    @JsonIgnore
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String name;
        private String description;
        private Group group;
        private List<Version> versions;

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

        public Builder versions(List<Version> versions) {
            this.versions = versions;
            return this;
        }

        public Box build() {
            Box box = new Box();
            box.versions = this.versions;
            box.name = this.name;
            box.description = this.description;
            box.group = this.group;
            return box;
        }
    }
}
