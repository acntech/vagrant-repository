package no.acntech.service;

import org.jooq.DSLContext;
import org.jooq.exception.NoDataFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import no.acntech.exception.ItemNotFoundException;
import no.acntech.exception.SaveItemFailedException;
import no.acntech.model.CreateUser;
import no.acntech.model.Password;
import no.acntech.model.UpdateUserPassword;
import no.acntech.model.UpdateUserRole;
import no.acntech.model.User;
import no.acntech.model.UserRole;

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

    public User getUser(@NotBlank String username) {
        LOGGER.debug("Get user for username {}", username);
        try (final var select = context.selectFrom(USERS)) {
            final var record = select.where(USERS.USERNAME.eq(username))
                    .fetchSingle();
            return conversionService.convert(record, User.class);
        } catch (NoDataFoundException e) {
            throw new ItemNotFoundException("No user found for username " + username, e);
        }
    }

    public List<User> findUsers() {
        LOGGER.debug("Find users");
        try (final var select = context.selectFrom(USERS)) {
            final var result = select.fetch();
            return result.stream()
                    .map(record -> conversionService.convert(record, User.class))
                    .collect(Collectors.toList());
        }
    }

    public void createUser(@Valid @NotNull final CreateUser createUser) {
        LOGGER.debug("Create user with username {}", createUser.username());
        final var password = passwordService.createPassword(createUser.password());
        try (final var insert = context.insertInto(USERS,
                USERS.USERNAME, USERS.ROLE, USERS.PASSWORD_HASH, USERS.PASSWORD_SALT, USERS.CREATED)) {
            final var rowsAffected = insert.values(createUser.username().toLowerCase(), UserRole.USER.name(),
                            password.passwordHash(), password.passwordSalt(), LocalDateTime.now())
                    .execute();
            LOGGER.debug("Insert into USERS table affected {} rows", rowsAffected);
            if (rowsAffected == 0) {
                throw new SaveItemFailedException("Failed to create user with username " + createUser.username());
            }
        }
    }

    public void deleteUser(@NotBlank String username) {
        LOGGER.debug("Delete user with username {}", username);
        // TODO: Verify if user is last member or last owner of organization
        try (final var delete = context.deleteFrom(USERS)) {
            final var rowsAffected = delete.where(USERS.USERNAME.eq(username))
                    .execute();
            LOGGER.debug("Delete record in USERS table affected {} rows", rowsAffected);
            if (rowsAffected == 0) {
                throw new SaveItemFailedException("Failed to delete user with username " + username);
            }
        }
    }

    public void updatePassword(@NotBlank String username,
                               @Valid @NotNull final UpdateUserPassword updateUserPassword) {
        LOGGER.debug("Update password for user with username {}", username);
        final var user = getUser(username);
        final var oldPassword = new Password(user.passwordHash(), user.passwordSalt());
        if (passwordService.verifyPassword(updateUserPassword.oldPassword(), oldPassword)) {
            final var newPassword = passwordService.createPassword(updateUserPassword.newPassword());
            try (final var update = context.update(USERS)
                    .set(USERS.PASSWORD_HASH, newPassword.passwordHash())
                    .set(USERS.PASSWORD_SALT, newPassword.passwordSalt())) {
                final var rowsAffected = update.where(USERS.USERNAME.eq(username))
                        .execute();
                LOGGER.debug("Updated record in USERS table affected {} rows", rowsAffected);
                if (rowsAffected == 0) {
                    throw new SaveItemFailedException("Failed to update password for user with username " + username);
                }
            }
        } else {
            throw new BadCredentialsException("Incorrect username and password combination");
        }
    }

    public void updateRole(@NotBlank String username,
                           @Valid @NotNull final UpdateUserRole updateUserRole) {
        LOGGER.debug("Update role for user with username {}", username);
        try (final var update = context.update(USERS).set(USERS.ROLE, updateUserRole.role().name())) {
            final var rowsAffected = update.where(USERS.USERNAME.eq(username))
                    .execute();
            LOGGER.debug("Updated record in USERS table affected {} rows", rowsAffected);
            if (rowsAffected == 0) {
                throw new SaveItemFailedException("Failed to update role for user with username " + username);
            }
        }
    }
}
