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

@Table(name = "VERSIONS")
@Entity
public class Version {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Column(unique = false)
    private String name;
    private String description;
    @ManyToOne
    @JoinColumn(name = "BOX_ID", nullable = false)
    private Box box;
    @OneToMany(mappedBy = "version", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Provider> providers;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Box getBox() {
        return box;
    }

    public List<Provider> getProviders() {
        return providers;
    }

    @JsonIgnore
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String name;
        private String description;
        private Box box;
        private List<Provider> providers;

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

        public Builder providers(List<Provider> providers) {
            this.providers = providers;
            return this;
        }

        public Version build() {
            Version version = new Version();
            version.providers = this.providers;
            version.description = this.description;
            version.name = this.name;
            version.box = this.box;
            return version;
        }
    }
}
