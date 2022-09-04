package no.acntech.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

public class User {

    private @NotNull Integer id;
    private @NotBlank String username;
    private @NotBlank String name;
    private @NotBlank Role role;
    private @NotBlank String passwordHash;
    private @NotBlank String passwordSalt;
    private @NotNull ZonedDateTime created;

    public Integer getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public Role getRole() {
        return role;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getPasswordSalt() {
        return passwordSalt;
    }

    public ZonedDateTime getCreated() {
        return created;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private @NotNull Integer id;
        private @NotBlank String username;
        private @NotBlank String name;
        private @NotBlank Role role;
        private @NotBlank String passwordHash;
        private @NotBlank String passwordSalt;
        private @NotNull ZonedDateTime created;

        private Builder() {
        }

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder role(Role role) {
            this.role = role;
            return this;
        }

        public Builder passwordHash(String passwordHash) {
            this.passwordHash = passwordHash;
            return this;
        }

        public Builder passwordSalt(String passwordSalt) {
            this.passwordSalt = passwordSalt;
            return this;
        }

        public Builder created(ZonedDateTime created) {
            this.created = created;
            return this;
        }

        public User build() {
            final var target = new User();
            target.username = this.username;
            target.name = this.name;
            target.role = this.role;
            target.passwordHash = this.passwordHash;
            target.passwordSalt = this.passwordSalt;
            target.created = this.created;
            target.id = this.id;
            return target;
        }
    }
}
