package no.acntech.service;

import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.jooq.exception.NoDataFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import no.acntech.exception.CannotDeleteItemException;
import no.acntech.exception.ItemNotFoundException;
import no.acntech.exception.SaveItemFailedException;
import no.acntech.model.AddOrganizationMember;
import no.acntech.model.CreateOrganization;
import no.acntech.model.Organization;
import no.acntech.model.OrganizationRole;
import no.acntech.model.UpdateOrganization;

import static no.acntech.model.tables.Members.MEMBERS;
import static no.acntech.model.tables.Organizations.ORGANIZATIONS;

@Validated
@Service
public class OrganizationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationService.class);
    private final DSLContext context;
    private final ConversionService conversionService;
    private final SecurityService securityService;
    private final UserService userService;
    private final OrganizationMemberService organizationMemberService;

    public OrganizationService(final DSLContext context,
                               final ConversionService conversionService,
                               final SecurityService securityService,
                               final UserService userService,
                               final OrganizationMemberService organizationMemberService) {
        this.context = context;
        this.conversionService = conversionService;
        this.securityService = securityService;
        this.userService = userService;
        this.organizationMemberService = organizationMemberService;
    }

    public Organization getOrganization(@NotBlank final String name) {
        LOGGER.debug("Get organization {}", name);
        try (final var select = context.selectFrom(ORGANIZATIONS)) {
            final var record = select.where(ORGANIZATIONS.NAME.eq(name))
                    .fetchSingle();
            return conversionService.convert(record, Organization.class);
        } catch (NoDataFoundException e) {
            throw new ItemNotFoundException("No organization found for name " + name, e);
        }
    }

    @Transactional
    public void createOrganization(@Valid @NotNull final CreateOrganization createOrganization) {
        LOGGER.debug("Create organization {}", createOrganization.name());
        final var username = securityService.getUsername();

        try (final var insert = context.insertInto(
                ORGANIZATIONS,
                ORGANIZATIONS.NAME,
                ORGANIZATIONS.DESCRIPTION,
                ORGANIZATIONS.CREATED)) {
            final var rowsAffected = insert
                    .values(
                            createOrganization.name().toLowerCase(),
                            createOrganization.description(), LocalDateTime.now())
                    .execute();
            LOGGER.debug("Insert into ORGANIZATIONS table affected {} rows", rowsAffected);
            if (rowsAffected == 0) {
                throw new SaveItemFailedException("Failed to create organization " + createOrganization.name());
            }
        }

        final var organization = getOrganization(createOrganization.name());
        final var user = userService.getUser(username);

        try (final var insert = context.insertInto(
                MEMBERS,
                MEMBERS.ORGANIZATION_ID,
                MEMBERS.USER_ID,
                MEMBERS.ROLE)) {
            final var rowsAffected = insert
                    .values(
                            organization.id(),
                            user.id(), OrganizationRole.OWNER.name())
                    .execute();
            LOGGER.debug("Insert into MEMBERS table affected {} rows", rowsAffected);
            if (rowsAffected == 0) {
                throw new SaveItemFailedException("Failed to create membership to organization " +
                        createOrganization.name() + " for user " + user.username());
            }
        }
    }

    @Transactional
    public void updateOrganization(@NotBlank final String name,
                                   @Valid @NotNull final UpdateOrganization updateOrganization) {
        LOGGER.info("Update organization {}", name);
        final var organization = getOrganization(name);
        final var newName = StringUtils.isBlank(updateOrganization.name()) ? organization.name() : updateOrganization.name();
        final var newDescription = StringUtils.isBlank(updateOrganization.description()) ? organization.description() : updateOrganization.description();

        try (final var update = context
                .update(ORGANIZATIONS)
                .set(ORGANIZATIONS.NAME, newName)
                .set(ORGANIZATIONS.DESCRIPTION, newDescription)
                .set(ORGANIZATIONS.MODIFIED, LocalDateTime.now())) {
            final var rowsAffected = update
                    .where(ORGANIZATIONS.ID.eq(organization.id()))
                    .execute();
            LOGGER.debug("Updated record in USERS table affected {} rows", rowsAffected);
            if (rowsAffected == 0) {
                throw new SaveItemFailedException("Failed to update organization " + name);
            }
        }
    }

    @Transactional
    public void deleteOrganization(@NotBlank final String name) {
        LOGGER.debug("Delete organizations {}", name);
        // TODO: Verify that organization has no boxes
        final var organization = getOrganization(name);
        try (final var delete = context.deleteFrom(ORGANIZATIONS)) {
            final var rowsAffected = delete
                    .where(ORGANIZATIONS.ID.eq(organization.id()))
                    .execute();
            LOGGER.debug("Delete record in ORGANIZATIONS table affected {} rows", rowsAffected);
            if (rowsAffected == 0) {
                throw new SaveItemFailedException("Failed to delete organization " + name);
            }
        }
    }

    @Transactional
    public void addOrganizationMember(@NotBlank final String name,
                                      @NotBlank final AddOrganizationMember addOrganizationMember) {
        LOGGER.info("Add member {} to organization {}", addOrganizationMember.username(), name);

        final var organization = getOrganization(name);
        final var user = userService.getUser(addOrganizationMember.username());

        final var rowsAffected = organizationMemberService.addMember(organization.id(), user.id(), addOrganizationMember.role());
        if (rowsAffected == 0) {
            throw new SaveItemFailedException("Failed to create membership to organization " +
                    name + " for user " + user.username());
        }
    }

    @Transactional
    public void removeOrganizationMember(@NotBlank final String name,
                                         @NotBlank final String username) {
        LOGGER.info("Remove member {} from organization {}", username, name);
        final var organizationMember = organizationMemberService.getMember(name, username);

        if (organizationMember.role() == OrganizationRole.OWNER) {
            final int numberOfOwners = organizationMemberService.getMemberCount(organizationMember.organizationId(), OrganizationRole.OWNER);
            if (numberOfOwners == 1) {
                throw new CannotDeleteItemException("Cannot delete membership of single organization owner");
            }
        }

        final var rowsAffected = organizationMemberService.removeMember(organizationMember.organizationId(), organizationMember.userId());
        if (rowsAffected == 0) {
            throw new SaveItemFailedException("Failed to delete organization " + name);
        }
    }
}
