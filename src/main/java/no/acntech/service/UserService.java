package no.acntech.service;

import org.apache.commons.lang3.StringUtils;
import org.jooq.exception.NoDataFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.ConversionService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

import no.acntech.annotation.Permission;
import no.acntech.exception.CannotDeleteItemException;
import no.acntech.exception.ItemAlreadyExistsException;
import no.acntech.exception.ItemNotFoundException;
import no.acntech.exception.SaveItemFailedException;
import no.acntech.model.Action;
import no.acntech.model.CreateUser;
import no.acntech.model.MemberRole;
import no.acntech.model.Resource;
import no.acntech.model.UpdateUser;
import no.acntech.model.User;
import no.acntech.model.UserRole;
import no.acntech.repository.MemberRepository;
import no.acntech.repository.UserRepository;

@Validated
@Service
public class UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    private final ConversionService conversionService;
    private final PasswordEncoder passwordEncoder;
    private final SecurityService securityService;
    private final UserRepository userRepository;
    private final MemberRepository memberRepository;

    public UserService(final ConversionService conversionService,
                       final PasswordEncoder passwordEncoder,
                       final SecurityService securityService,
                       final UserRepository userRepository,
                       final MemberRepository memberRepository) {
        this.conversionService = conversionService;
        this.passwordEncoder = passwordEncoder;
        this.securityService = securityService;
        this.userRepository = userRepository;
        this.memberRepository = memberRepository;
    }

    @Permission(action = Action.READ, resource = Resource.USERS)
    public User getUser(@NotBlank final String username) {
        LOGGER.debug("Get user {}", username);
        try {
            final var record = userRepository.getUser(username);
            return conversionService.convert(record, User.class);
        } catch (NoDataFoundException e) {
            throw new ItemNotFoundException("No user found for username " + username, e);
        }
    }

    @Permission(action = Action.READ, resource = Resource.USERS)
    public List<User> findUsers() {
        LOGGER.debug("Find users");
        final var result = userRepository.findUsers();
        return result.stream()
                .map(record -> conversionService.convert(record, User.class))
                .collect(Collectors.toList());
    }

    @Permission(action = Action.CREATE, resource = Resource.USERS)
    @Transactional
    public void createUser(@Valid @NotNull final CreateUser createUser) {
        LOGGER.debug("Create user {}", createUser.username());
        try {
            final var passwordHash = passwordEncoder.encode(createUser.password());
            final var rowsAffected = userRepository.createUser(
                    createUser.username().toLowerCase(),
                    createUser.role().name(),
                    passwordHash);
            LOGGER.debug("Insert into USERS table affected {} rows", rowsAffected);
            if (rowsAffected == 0) {
                throw new SaveItemFailedException("Failed to create user " + createUser.username());
            }
        } catch (DuplicateKeyException e) {
            throw new ItemAlreadyExistsException("User " + createUser.username() + " already exists", e);
        }
    }

    @Permission(action = Action.UPDATE, resource = Resource.USERS)
    @Transactional
    public void updateUser(@NotBlank final String username,
                           @Valid @NotNull final UpdateUser updateUser) {
        LOGGER.debug("Update user {}", username);
        final var user = getUser(username);

        if (StringUtils.isNoneBlank(updateUser.newPassword()) && !securityService.hasRole(UserRole.ADMIN)) {
            Assert.hasText(updateUser.oldPassword(), "Missing passwords");
            if (!passwordEncoder.matches(updateUser.oldPassword(), user.passwordHash())) {
                throw new BadCredentialsException("Incorrect username and password combination");
            }
        }

        final var newUsername = StringUtils.isBlank(updateUser.username()) ? user.username() : updateUser.username();
        final var newRole = updateUser.role() == null ? user.role() : updateUser.role();
        final var newPasswordHash = StringUtils.isBlank(updateUser.newPassword()) ?
                user.passwordHash() : passwordEncoder.encode(updateUser.newPassword());

        final var rowsAffected = userRepository.updateUser(
                username,
                newUsername.toLowerCase(),
                newRole.name(),
                newPasswordHash);
        LOGGER.debug("Updated record in USERS table affected {} rows", rowsAffected);
        if (rowsAffected == 0) {
            throw new SaveItemFailedException("Failed to update user " + username);
        }
    }

    @Permission(action = Action.DELETE, resource = Resource.USERS)
    public void deleteUser(@NotBlank final String username) {
        LOGGER.debug("Delete user {}", username);
        final var memberships = memberRepository.findMemberships(username);
        if (!memberships.isEmpty()) {
            for (var membership : memberships) {
                final var numberOfOwners = memberRepository.getMemberCount(membership.getOrganizationId(), MemberRole.OWNER);
                if (numberOfOwners == 1) {
                    throw new CannotDeleteItemException("Cannot delete membership of last organization owner");
                }
            }
        }
        final var rowsAffected = userRepository.deleteUser(username);
        LOGGER.debug("Delete record in USERS table affected {} rows", rowsAffected);
        if (rowsAffected == 0) {
            throw new SaveItemFailedException("Failed to delete user " + username);
        }
    }
}
