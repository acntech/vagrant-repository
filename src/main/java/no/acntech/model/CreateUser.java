package no.acntech.model;

import javax.validation.constraints.NotBlank;

public class CreateUser {

    private @NotBlank String username;
    private @NotBlank String name;
    private @NotBlank String password;

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private @NotBlank String username;
        private @NotBlank String name;
        private @NotBlank String password;

        private Builder() {
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public CreateUser build() {
            var target = new CreateUser();
            target.name = this.name;
            target.password = this.password;
            target.username = this.username;
            return target;
        }
    }
}
