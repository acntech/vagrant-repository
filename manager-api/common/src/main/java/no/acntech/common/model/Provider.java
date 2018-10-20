package no.acntech.common.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Table(name = "PROVIDERS")
@Entity
public class Provider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private ProviderType type;
    @NotNull
    private Long size;
    @Transient
    private ZonedDateTime created;
    @Transient
    private ZonedDateTime modified;
    @ManyToOne
    @JoinColumn(name = "VERSION_ID", nullable = false)
    private Version version;

    public Long getId() {
        return id;
    }

    public ProviderType getType() {
        return type;
    }

    public Long getSize() {
        return size;
    }

    public ZonedDateTime getCreated() {
        return created;
    }

    public ZonedDateTime getModified() {
        return modified;
    }

    public Version getVersion() {
        return version;
    }

    @JsonIgnore
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private ProviderType type;
        private Long size;
        private Version version;

        private Builder() {
        }

        public Builder type(ProviderType type) {
            this.type = type;
            return this;
        }

        public Builder size(Long size) {
            this.size = size;
            return this;
        }

        public Builder version(Version version) {
            this.version = version;
            return this;
        }

        public Builder from(Provider provider) {
            if (provider != null) {
                this.type = provider.type;
                this.size = provider.size;
                this.version = provider.version;
            }
            return this;
        }

        public Provider build() {
            Provider provider = new Provider();
            provider.type = this.type;
            provider.size = this.size;
            provider.version = this.version;
            return provider;
        }
    }
}
