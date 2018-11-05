package no.acntech.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ClientProvider {

    private String name;
    private String url;
    @JsonProperty("checksum_type")
    private String checksumType;
    private String checksum;

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getChecksumType() {
        return checksumType;
    }

    public String getChecksum() {
        return checksum;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String name;
        private String url;
        private String checksumType;
        private String checksum;

        private Builder() {
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder checksumType(String checksumType) {
            this.checksumType = checksumType;
            return this;
        }

        public Builder checksum(String checksum) {
            this.checksum = checksum;
            return this;
        }

        public ClientProvider build() {
            ClientProvider clientProvider = new ClientProvider();
            clientProvider.name = this.name;
            clientProvider.checksumType = this.checksumType;
            clientProvider.url = this.url;
            clientProvider.checksum = this.checksum;
            return clientProvider;
        }
    }
}
