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
import java.util.List;
import java.util.stream.Collectors;

import no.acntech.annotation.Permission;
import no.acntech.exception.ItemNotFoundException;
import no.acntech.exception.SaveItemFailedException;
import no.acntech.model.Action;
import no.acntech.model.CreateOrganization;
import no.acntech.model.MemberRole;
import no.acntech.model.Organization;
import no.acntech.model.Resource;
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

    @Permission(action = Action.READ, resource = Resource.ORGANIZATIONS)
    public Organization getOrganization(@NotBlank final String name) {
        LOGGER.debug("Get organization {}", name);
        try {
            final var record = organizationRepository.getOrganization(name);
            if (record.getPrivate()) {
                final var username = securityService.getUsername();
                memberRepository.getMember(name, username); // Will throw exception if user is not member
            }
            return conversionService.convert(record, Organization.class);
        } catch (NoDataFoundException e) {
            throw new ItemNotFoundException("No organization found for name " + name, e);
        }
    }

    @Permission(action = Action.READ, resource = Resource.ORGANIZATIONS)
    public List<Organization> findOrganizations(@NotBlank final String username) {
        LOGGER.debug("Find organizations for user {}", username);
        final var result = organizationRepository.findOrganizationsForUser(username);
        return result.stream()
                .map(record -> conversionService.convert(record, Organization.class))
                .collect(Collectors.toList());
    }

    @Permission(action = Action.CREATE, resource = Resource.ORGANIZATIONS)
    @Transactional
    public void createOrganization(@NotBlank final String username,
                                   @Valid @NotNull final CreateOrganization createOrganization) {
        LOGGER.debug("Create organization {}", createOrganization.name());
        final var user = userService.getUser(username);

        var rowsAffected = organizationRepository.createOrganization(
                createOrganization.name().toLowerCase(),
                createOrganization.description(),
                createOrganization.isPrivate());
        LOGGER.debug("Insert into ORGANIZATIONS table affected {} rows", rowsAffected);
        if (rowsAffected == 0) {
            throw new SaveItemFailedException("Failed to create organization " + createOrganization.name().toLowerCase());
        }

        final var organization = getOrganization(createOrganization.name().toLowerCase());

        rowsAffected = memberRepository.addMember(
                organization.id(),
                user.id(),
                MemberRole.OWNER
        );
        LOGGER.debug("Insert into MEMBERS table affected {} rows", rowsAffected);
        if (rowsAffected == 0) {
            throw new SaveItemFailedException("Failed to create membership to organization " +
                    createOrganization.name() + " for user " + user.username());
        }
    }

    @Permission(action = Action.UPDATE, resource = Resource.ORGANIZATIONS)
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
                newDescription,
                updateOrganization.isPrivate()
        );
        LOGGER.debug("Updated record in USERS table affected {} rows", rowsAffected);
        if (rowsAffected == 0) {
            throw new SaveItemFailedException("Failed to update organization " + name);
        }
    }

    @Permission(action = Action.DELETE, resource = Resource.ORGANIZATIONS)
    @Transactional
    public void deleteOrganization(@NotBlank final String name) {
        LOGGER.debug("Delete organizations {}", name);
        final var rowsAffected = organizationRepository.deleteOrganization(name);
        LOGGER.debug("Delete record in ORGANIZATIONS table affected {} rows", rowsAffected);
        if (rowsAffected == 0) {
            throw new SaveItemFailedException("Failed to delete organization " + name);
        }
    }
}
