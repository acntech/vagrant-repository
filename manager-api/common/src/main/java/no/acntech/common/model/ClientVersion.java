package no.acntech.common.model;

import java.util.List;

public class ClientVersion {

    private String version;
    private List<ClientProvider> providers;

    public String getVersion() {
        return version;
    }

    public List<ClientProvider> getProviders() {
        return providers;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String version;
        private List<ClientProvider> providers;

        private Builder() {
        }

        public Builder version(String version) {
            this.version = version;
            return this;
        }

        public Builder providers(List<ClientProvider> providers) {
            this.providers = providers;
            return this;
        }

        public ClientVersion build() {
            ClientVersion clientVersion = new ClientVersion();
            clientVersion.providers = this.providers;
            clientVersion.version = this.version;
            return clientVersion;
        }
    }
}
