package no.acntech.service;

import org.jooq.DSLContext;
import org.jooq.exception.NoDataFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import no.acntech.model.CreateUser;
import no.acntech.model.Role;
import no.acntech.model.UpdateUserPassword;
import no.acntech.model.UpdateUserRole;
import no.acntech.model.User;

import static no.acntech.model.tables.Users.USERS;

@Validated
@Service
public class UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    private final ConversionService conversionService;
    private final DSLContext context;
    private final PasswordService passwordService;

    public UserService(final ConversionService conversionService,
                       final DSLContext context,
                       final PasswordService passwordService) {
        this.conversionService = conversionService;
        this.context = context;
        this.passwordService = passwordService;
    }

    public Optional<User> getUser(@NotBlank String username) {
        try (final var select = context.selectFrom(USERS)) {
            final var record = select.where(USERS.USERNAME.eq(username))
                    .fetchSingle();
            final var user = conversionService.convert(record, User.class);
            return Optional.ofNullable(user);
        } catch (NoDataFoundException e) {
            return Optional.empty();
        }
    }

    public List<User> findUsers() {
        try (final var select = context.selectFrom(USERS)) {
            final var result = select.fetch();
            return result.stream()
                    .map(record -> conversionService.convert(record, User.class))
                    .collect(Collectors.toList());
        }
    }

    public void createUser(@Valid @NotNull final CreateUser createUser) {
        final var password = passwordService.createPassword(createUser.getPassword());
        try (final var insert = context.insertInto(USERS,
                USERS.USERNAME, USERS.NAME, USERS.ROLE, USERS.PASSWORD_HASH, USERS.PASSWORD_SALT)) {
            final var rowsAffected = insert.values(createUser.getUsername(), createUser.getName(), Role.USER.name(),
                            password.passwordHash(), password.passwordSalt())
                    .execute();
            LOGGER.debug("Insert into USERS table affected {} rows", rowsAffected);
        }
    }

    public void updatePassword(@Valid @NotNull final UpdateUserPassword updateUserPassword) {
        final var password = passwordService.createPassword(updateUserPassword.password());
        try (final var update = context.update(USERS)
                .set(USERS.PASSWORD_HASH, password.passwordHash())
                .set(USERS.PASSWORD_SALT, password.passwordSalt())) {
            final var rowsAffected = update.where(USERS.USERNAME.eq(updateUserPassword.username()))
                    .execute();
            LOGGER.debug("Updated record in USERS table affected {} rows", rowsAffected);
        }
    }

    public void updateRole(@Valid @NotNull final UpdateUserRole updateUserRole) {
        try (final var update = context.update(USERS).set(USERS.ROLE, updateUserRole.role().name())) {
            final var rowsAffected = update.where(USERS.USERNAME.eq(updateUserRole.username()))
                    .execute();
            LOGGER.debug("Updated record in USERS table affected {} rows", rowsAffected);
        }
    }
}
