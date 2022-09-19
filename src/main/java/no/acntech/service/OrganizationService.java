package no.acntech.service;

import org.apache.commons.lang3.StringUtils;
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

import no.acntech.exception.CannotDeleteItemException;
import no.acntech.exception.ItemNotFoundException;
import no.acntech.exception.SaveItemFailedException;
import no.acntech.model.AddOrganizationMember;
import no.acntech.model.CreateOrganization;
import no.acntech.model.Organization;
import no.acntech.model.OrganizationRole;
import no.acntech.model.UpdateOrganization;
import no.acntech.repository.MemberRepository;
import no.acntech.repository.OrganizationRepository;

@Validated
@Service
public class OrganizationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationService.class);
    private final ConversionService conversionService;
    private final SecurityService securityService;
    private final UserService userService;
    private final OrganizationRepository organizationRepository;
    private final MemberRepository memberRepository;

    public OrganizationService(final ConversionService conversionService,
                               final SecurityService securityService,
                               final UserService userService,
                               final OrganizationRepository organizationRepository,
                               final MemberRepository memberRepository) {
        this.conversionService = conversionService;
        this.securityService = securityService;
        this.userService = userService;
        this.organizationRepository = organizationRepository;
        this.memberRepository = memberRepository;
    }

    public Organization getOrganization(@NotNull final Integer id) {
        LOGGER.debug("Get organization for ID {}", id);
        try {
            final var record = organizationRepository.getOrganization(id);
            return conversionService.convert(record, Organization.class);
        } catch (NoDataFoundException e) {
            throw new ItemNotFoundException("No organization found for ID " + id, e);
        }
    }

    public Organization getOrganization(@NotBlank final String name) {
        LOGGER.debug("Get organization {}", name);
        try {
            final var record = organizationRepository.getOrganization(name);
            return conversionService.convert(record, Organization.class);
        } catch (NoDataFoundException e) {
            throw new ItemNotFoundException("No organization found for name " + name, e);
        }
    }

    @Transactional
    public void createOrganization(@Valid @NotNull final CreateOrganization createOrganization) {
        LOGGER.debug("Create organization {}", createOrganization.name());
        final var username = securityService.getUsername();

        var rowsAffected = organizationRepository.createOrganization(
                createOrganization.name().toLowerCase(),
                createOrganization.description());
        LOGGER.debug("Insert into ORGANIZATIONS table affected {} rows", rowsAffected);
        if (rowsAffected == 0) {
            throw new SaveItemFailedException("Failed to create organization " + createOrganization.name());
        }

        final var organization = getOrganization(createOrganization.name());
        final var user = userService.getUser(username);

        rowsAffected = memberRepository.addMember(
                organization.id(),
                user.id(),
                OrganizationRole.OWNER
        );
        LOGGER.debug("Insert into MEMBERS table affected {} rows", rowsAffected);
        if (rowsAffected == 0) {
            throw new SaveItemFailedException("Failed to create membership to organization " +
                    createOrganization.name() + " for user " + user.username());
        }
    }

    @Transactional
    public void updateOrganization(@NotBlank final String name,
                                   @Valid @NotNull final UpdateOrganization updateOrganization) {
        LOGGER.info("Update organization {}", name);
        final var organization = getOrganization(name);
        final var newName = StringUtils.isBlank(updateOrganization.name()) ? organization.name() : updateOrganization.name();
        final var newDescription = StringUtils.isBlank(updateOrganization.description()) ? organization.description() : updateOrganization.description();

        final var rowsAffected = organizationRepository.updateOrganization(
                name,
                newName,
                newDescription
        );
        LOGGER.debug("Updated record in USERS table affected {} rows", rowsAffected);
        if (rowsAffected == 0) {
            throw new SaveItemFailedException("Failed to update organization " + name);
        }
    }

    @Transactional
    public void deleteOrganization(@NotBlank final String name) {
        LOGGER.debug("Delete organizations {}", name);
        // TODO: Verify that organization has no boxes
        final var rowsAffected = organizationRepository.deleteOrganization(name);
        LOGGER.debug("Delete record in ORGANIZATIONS table affected {} rows", rowsAffected);
        if (rowsAffected == 0) {
            throw new SaveItemFailedException("Failed to delete organization " + name);
        }
    }

    @Transactional
    public void addOrganizationMember(@NotBlank final String name,
                                      @NotBlank final AddOrganizationMember addOrganizationMember) {
        LOGGER.info("Add member {} to organization {}", addOrganizationMember.username(), name);

        final var organization = getOrganization(name);
        final var user = userService.getUser(addOrganizationMember.username());

        final var rowsAffected = memberRepository.addMember(organization.id(), user.id(), addOrganizationMember.role());
        if (rowsAffected == 0) {
            throw new SaveItemFailedException("Failed to create membership to organization " +
                    name + " for user " + user.username());
        }
    }

    @Transactional
    public void removeOrganizationMember(@NotBlank final String name,
                                         @NotBlank final String username) {
        LOGGER.info("Remove member {} from organization {}", username, name);
        final var organizationMember = memberRepository.getMember(name, username);

        if (OrganizationRole.OWNER.name().equals(organizationMember.getRole())) {
            final int numberOfOwners = memberRepository.getMemberCount(organizationMember.getOrganizationId(), OrganizationRole.OWNER);
            if (numberOfOwners == 1) {
                throw new CannotDeleteItemException("Cannot delete membership of single organization owner");
            }
        }

        final var rowsAffected = memberRepository.removeMember(organizationMember.getOrganizationId(), organizationMember.getUserId());
        if (rowsAffected == 0) {
            throw new SaveItemFailedException("Failed to delete organization " + name);
        }
    }
}
