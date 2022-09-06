package no.acntech.service;

import org.jooq.DSLContext;
import org.jooq.exception.NoDataFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import no.acntech.exception.CannotDeleteItemException;
import no.acntech.exception.ItemNotFoundException;
import no.acntech.exception.SaveItemFailedException;
import no.acntech.model.CreateOrganization;
import no.acntech.model.Organization;
import no.acntech.model.OrganizationMember;
import no.acntech.model.OrganizationRole;
import no.acntech.model.UpdateOrganization;

import static no.acntech.model.tables.Members.MEMBERS;
import static no.acntech.model.tables.Organizations.ORGANIZATIONS;
import static no.acntech.model.tables.Users.USERS;

@Service
public class OrganizationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationService.class);

    private final ConversionService conversionService;
    private final DSLContext context;
    private final SecurityService securityService;
    private final UserService userService;

    public OrganizationService(final ConversionService conversionService,
                               final DSLContext context,
                               final SecurityService securityService,
                               final UserService userService) {
        this.conversionService = conversionService;
        this.context = context;
        this.securityService = securityService;
        this.userService = userService;
    }

    public Organization getOrganization(@NotBlank final String name) {
        LOGGER.debug("Get organization for name {}", name);
        try (final var select = context.selectFrom(ORGANIZATIONS)) {
            final var record = select.where(ORGANIZATIONS.NAME.eq(name))
                    .fetchSingle();
            return conversionService.convert(record, Organization.class);
        } catch (NoDataFoundException e) {
            throw new ItemNotFoundException("No organization found for name " + name, e);
        }
    }

    public List<Organization> findOrganizations() {
        LOGGER.debug("Find organizations");
        try (final var select = context.selectFrom(ORGANIZATIONS)) {
            final var result = select.fetch();
            return result.stream()
                    .map(record -> conversionService.convert(record, Organization.class))
                    .collect(Collectors.toList());
        }
    }

    @Transactional
    public void createOrganization(@Valid @NotNull final CreateOrganization createOrganization) {
        LOGGER.debug("Create organization with name {}", createOrganization.name());
        final var username = securityService.getUsername();

        try (final var insert = context.insertInto(ORGANIZATIONS,
                ORGANIZATIONS.NAME, ORGANIZATIONS.DESCRIPTION, ORGANIZATIONS.CREATED)) {
            final var rowsAffected = insert.values(createOrganization.name().toLowerCase(),
                            createOrganization.description(), LocalDateTime.now())
                    .execute();
            LOGGER.debug("Insert into ORGANIZATIONS table affected {} rows", rowsAffected);
            if (rowsAffected == 0) {
                throw new SaveItemFailedException("Failed to create organization with name " + createOrganization.name());
            }
        }

        final var organization = getOrganization(createOrganization.name());
        final var user = userService.getUser(username);

        try (final var insert = context.insertInto(MEMBERS,
                MEMBERS.ORGANIZATION_ID, MEMBERS.USER_ID, MEMBERS.ROLE)) {
            final var rowsAffected = insert.values(organization.id(),
                            user.id(), OrganizationRole.OWNER.name())
                    .execute();
            LOGGER.debug("Insert into MEMBERS table affected {} rows", rowsAffected);
            if (rowsAffected == 0) {
                throw new SaveItemFailedException("Failed to create membership to organization with name " +
                        createOrganization.name() + " for user with username " + user.username());
            }
        }
    }

    @Transactional
    public void updateOrganization(@NotBlank final String name,
                                   @Valid final UpdateOrganization updateOrganization) {
        LOGGER.info("Update organization with name {}", name);
        try (final var update = context.update(ORGANIZATIONS)
                .set(ORGANIZATIONS.DESCRIPTION, updateOrganization.description())) {
            final var rowsAffected = update.where(ORGANIZATIONS.NAME.eq(name))
                    .execute();
            LOGGER.debug("Updated record in USERS table affected {} rows", rowsAffected);
            if (rowsAffected == 0) {
                throw new SaveItemFailedException("Failed to update organization with name " + name);
            }
        }
    }

    @Transactional
    public void deleteOrganization(@NotBlank final String name) {
        LOGGER.debug("Delete organizations with name {}", name);
        // TODO: Verify if organization has any boxes
        try (final var delete = context.deleteFrom(ORGANIZATIONS)) {
            final var rowsAffected = delete.where(ORGANIZATIONS.NAME.eq(name))
                    .execute();
            LOGGER.debug("Delete record in ORGANIZATIONS table affected {} rows", rowsAffected);
            if (rowsAffected == 0) {
                throw new SaveItemFailedException("Failed to delete organization with name " + name);
            }
        }
    }

    @Transactional
    public void addOrganizationMember(@NotBlank final String name,
                                      @NotBlank final OrganizationMember organizationMember) {
        LOGGER.info("Add member with username {} to organization with name {}", organizationMember.username(), name);

        final var organization = getOrganization(name);
        final var user = userService.getUser(organizationMember.username());

        try (final var insert = context.insertInto(MEMBERS,
                MEMBERS.ORGANIZATION_ID, MEMBERS.USER_ID, MEMBERS.ROLE)) {
            final var rowsAffected = insert.values(organization.id(),
                            user.id(), organizationMember.role().name())
                    .execute();
            LOGGER.debug("Insert into MEMBERS table affected {} rows", rowsAffected);
            if (rowsAffected == 0) {
                throw new SaveItemFailedException("Failed to create membership to organization with name " +
                        name + " for user with username " + user.username());
            }
        }
    }

    @Transactional
    public void removeOrganizationMember(@NotBlank final String name,
                                         @NotBlank final String username) {
        LOGGER.info("Remove member with username {} from organization with name {}", username, name);

        /*
         * 1. Check if user is an organization owner
         * 2. If so, check if there are other owners
         * 3. If organization have no other owners throw exception
         * 4. Else delete membership
         */

        try (final var select = context.select(ORGANIZATIONS.ID, USERS.ID, MEMBERS.ID, MEMBERS.ROLE)) {
            try (final var join1 = select.from(MEMBERS).join(ORGANIZATIONS).on(MEMBERS.ORGANIZATION_ID.eq(ORGANIZATIONS.ID))) {
                try (final var join2 = join1.join(USERS).on(MEMBERS.USER_ID.eq(USERS.ID))) {
                    final var record = join2
                            .where(ORGANIZATIONS.NAME.eq(name))
                            .and(USERS.USERNAME.eq(username))
                            .fetchSingle();
                    final var organizationId = record.get(ORGANIZATIONS.ID);
                    final var membershipId = record.get(MEMBERS.ID);
                    final var role = OrganizationRole.valueOf(record.get(MEMBERS.ROLE));

                    if (role == OrganizationRole.OWNER) {
                        try (final var count = context.selectCount()) {
                            final int numberOfOwners = count.from(MEMBERS)
                                    .where(MEMBERS.ORGANIZATION_ID.eq(organizationId))
                                    .and(MEMBERS.ROLE.eq(OrganizationRole.OWNER.name()))
                                    .execute();
                            if (numberOfOwners == 1) {
                                throw new CannotDeleteItemException("Cannot delete membership of single organization owner");
                            }
                        }
                    }

                    try (final var delete = context.deleteFrom(MEMBERS)) {
                        final var rowsAffected = delete
                                .where(MEMBERS.ID.eq(membershipId))
                                .execute();
                        LOGGER.debug("Delete record in ORGANIZATIONS table affected {} rows", rowsAffected);
                        if (rowsAffected == 0) {
                            throw new SaveItemFailedException("Failed to delete organization with name " + name);
                        }
                    }
                }
            }
        } catch (NoDataFoundException e) {
            throw new ItemNotFoundException("No membership found to organization with name " + name +
                    " for user with username " + username, e);
        }
    }
}
