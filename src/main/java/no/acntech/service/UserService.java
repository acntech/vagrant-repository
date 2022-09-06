package no.acntech.service;

import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.jooq.exception.NoDataFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
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
import no.acntech.model.UpdateUser;
import no.acntech.model.User;

import static no.acntech.model.tables.Users.USERS;

@Validated
@Service
public class UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    private final DSLContext context;
    private final ConversionService conversionService;
    private final PasswordService passwordService;

    public UserService(final DSLContext context,
                       final ConversionService conversionService,
                       final PasswordService passwordService) {
        this.context = context;
        this.conversionService = conversionService;
        this.passwordService = passwordService;
    }

    public User getUser(@NotBlank final String username) {
        LOGGER.debug("Get user {}", username);
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
        LOGGER.debug("Create user {}", createUser.username());
        // TODO: Access control for who can create admin users
        final var password = passwordService.createPassword(createUser.password());
        try (final var insert = context.insertInto(
                USERS,
                USERS.USERNAME,
                USERS.ROLE,
                USERS.PASSWORD_HASH,
                USERS.PASSWORD_SALT,
                USERS.CREATED)) {
            final var rowsAffected = insert.
                    values(
                            createUser.username().toLowerCase(),
                            createUser.role().name(),
                            password.passwordHash(),
                            password.passwordSalt(),
                            LocalDateTime.now())
                    .execute();
            LOGGER.debug("Insert into USERS table affected {} rows", rowsAffected);
            if (rowsAffected == 0) {
                throw new SaveItemFailedException("Failed to create user " + createUser.username());
            }
        }
    }

    public void updateUser(@NotBlank final String username,
                           @Valid @NotNull final UpdateUser updateUser) {
        LOGGER.debug("Update password for user {}", username);
        final var user = getUser(username);
        final var newUsername = StringUtils.isBlank(updateUser.username()) ? user.username() : updateUser.username();
        final var newRole = updateUser.role() == null ? user.role() : updateUser.role();
        final var oldPassword = new Password(user.passwordHash(), user.passwordSalt());
        final var newPassword = StringUtils.isNoneBlank(updateUser.oldPassword()) && StringUtils.isNoneBlank(updateUser.newPassword()) ?
                oldPassword : passwordService.createPassword(updateUser.newPassword());

        if (StringUtils.isNoneBlank(updateUser.oldPassword())) {
            Assert.hasText(updateUser.newPassword(), "Missing passwords");
            if (!passwordService.verifyPassword(updateUser.oldPassword(), oldPassword)) {
                throw new BadCredentialsException("Incorrect username and password combination");
            }
        }

        try (final var update = context
                .update(USERS)
                .set(USERS.USERNAME, newUsername)
                .set(USERS.ROLE, newRole.name())
                .set(USERS.PASSWORD_HASH, newPassword.passwordHash())
                .set(USERS.PASSWORD_SALT, newPassword.passwordSalt())
                .set(USERS.MODIFIED, LocalDateTime.now())) {
            final var rowsAffected = update
                    .where(USERS.ID.eq(user.id()))
                    .execute();
            LOGGER.debug("Updated record in USERS table affected {} rows", rowsAffected);
            if (rowsAffected == 0) {
                throw new SaveItemFailedException("Failed to update password for user " + username);
            }
        }
    }

    public void deleteUser(@NotBlank final String username) {
        LOGGER.debug("Delete user {}", username);
        // TODO: Verify that user isn't last member or last owner of organization
        final var user = getUser(username);
        try (final var delete = context.deleteFrom(USERS)) {
            final var rowsAffected = delete
                    .where(USERS.ID.eq(user.id()))
                    .execute();
            LOGGER.debug("Delete record in USERS table affected {} rows", rowsAffected);
            if (rowsAffected == 0) {
                throw new SaveItemFailedException("Failed to delete user " + username);
            }
        }
    }
}
