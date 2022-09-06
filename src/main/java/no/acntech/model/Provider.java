package no.acntech.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

public class Provider {

    private Long id;
    @NotNull
    private ProviderType providerType;
    private Long size;
    private Algorithm checksumType;
    private String checksum;
    private ZonedDateTime created;
    private ZonedDateTime modified;
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

    public Algorithm getChecksumType() {
        return checksumType;
    }

    public void setChecksumType(Algorithm checksumType) {
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
        private Algorithm checksumType;
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

        public Builder checksumType(Algorithm checksumType) {
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
