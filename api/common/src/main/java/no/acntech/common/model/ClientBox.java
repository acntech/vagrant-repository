package no.acntech.common.model;

import java.util.List;

public class ClientBox {

    private String name;
    private String description;
    private List<ClientVersion> versions;

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<ClientVersion> getVersions() {
        return versions;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String name;
        private String description;
        private List<ClientVersion> versions;

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

        public Builder versions(List<ClientVersion> versions) {
            this.versions = versions;
            return this;
        }

        public ClientBox build() {
            ClientBox clientBox = new ClientBox();
            clientBox.description = this.description;
            clientBox.name = this.name;
            clientBox.versions = this.versions;
            return clientBox;
        }
    }
}
