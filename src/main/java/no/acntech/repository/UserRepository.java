package no.acntech.repository;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

import no.acntech.model.tables.records.UsersRecord;

import static no.acntech.model.tables.Users.USERS;

@Validated
@Repository
public class UserRepository {

    private final DSLContext context;

    public UserRepository(final DSLContext context) {
        this.context = context;
    }

    public UsersRecord getUser(@NotBlank final String username) {
        try (final var select = context.selectFrom(USERS)) {
            return select.where(USERS.USERNAME.eq(username))
                    .fetchSingle();
        }
    }

    public Result<UsersRecord> findUsers() {
        try (final var select = context.selectFrom(USERS)) {
            return select.fetch();
        }
    }

    @Transactional
    public int createUser(@NotBlank final String username,
                          @NotBlank final String role,
                          @NotBlank final String passwordHash) {
        try (final var insert = context.insertInto(
                USERS,
                USERS.USERNAME,
                USERS.ROLE,
                USERS.PASSWORD_HASH,
                USERS.CREATED)) {
            return insert.
                    values(
                            username,
                            role,
                            passwordHash,
                            LocalDateTime.now())
                    .execute();
        }
    }

    @Transactional
    public int updateUser(@NotBlank final String oldUsername,
                          @NotBlank final String newUsername,
                          @NotBlank final String role,
                          @NotBlank final String passwordHash) {
        try (final var update = context
                .update(USERS)
                .set(USERS.USERNAME, newUsername)
                .set(USERS.ROLE, role)
                .set(USERS.PASSWORD_HASH, passwordHash)
                .set(USERS.MODIFIED, LocalDateTime.now())) {
            return update
                    .where(USERS.USERNAME.eq(oldUsername))
                    .execute();
        }
    }

    @Transactional
    public int deleteUser(@NotBlank final String username) {
        try (final var delete = context.deleteFrom(USERS)) {
            return delete
                    .where(USERS.USERNAME.eq(username))
                    .execute();
        }
    }
}
