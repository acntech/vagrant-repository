package no.acntech.common.model;

import javax.persistence.*;
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
    @Enumerated(EnumType.STRING)
    private ProviderType providerType;
    private Long size;
    @Enumerated(EnumType.STRING)
    private ChecksumType checksumType;
    private String checksum;
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

    public ProviderType getProviderType() {
        return providerType;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public ChecksumType getChecksumType() {
        return checksumType;
    }

    public void setChecksumType(ChecksumType checksumType) {
        this.checksumType = checksumType;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
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

        private ProviderType providerType;
        private Long size;
        private ChecksumType checksumType;
        private String checksum;
        private Version version;

        private Builder() {
        }

        public Builder providerType(ProviderType providerType) {
            this.providerType = providerType;
            return this;
        }

        public Builder size(Long size) {
            this.size = size;
            return this;
        }

        public Builder checksumType(ChecksumType checksumType) {
            this.checksumType = checksumType;
            return this;
        }

        public Builder checksum(String checksum) {
            this.checksum = checksum;
            return this;
        }

        public Builder version(Version version) {
            this.version = version;
            return this;
        }

        public Provider build() {
            Provider provider = new Provider();
            provider.providerType = this.providerType;
            provider.size = this.size;
            provider.checksumType = this.checksumType;
            provider.checksum = this.checksum;
            provider.version = this.version;
            return provider;
        }
    }
}
