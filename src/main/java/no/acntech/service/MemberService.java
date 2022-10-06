package no.acntech.service;

import org.jooq.exception.NoDataFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotBlank;

import no.acntech.annotation.Permission;
import no.acntech.exception.CannotDeleteItemException;
import no.acntech.exception.SaveItemFailedException;
import no.acntech.model.Action;
import no.acntech.model.AddMember;
import no.acntech.model.MemberRole;
import no.acntech.model.Resource;
import no.acntech.repository.MemberRepository;
import no.acntech.repository.OrganizationRepository;
import no.acntech.repository.UserRepository;

@Service
public class MemberService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MemberService.class);
    private final MemberRepository memberRepository;
    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;

    public MemberService(final MemberRepository memberRepository,
                         final OrganizationRepository organizationRepository,
                         final UserRepository userRepository) {
        this.memberRepository = memberRepository;
        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
    }

    @Permission(action = Action.CREATE, resource = Resource.MEMBERS)
    @Transactional
    public void addMember(@NotBlank final String name,
                          @NotBlank final AddMember addOrganizationMember) {
        LOGGER.info("Add member {} to organization {}", addOrganizationMember.username(), name);

        try {
            final var organizationsRecord = organizationRepository.getOrganization(name);
            final var usersRecord = userRepository.getUser(addOrganizationMember.username());

            final var rowsAffected = memberRepository.addMember(
                    organizationsRecord.getId(),
                    usersRecord.getId(),
                    addOrganizationMember.role());
            if (rowsAffected == 0) {
                throw new SaveItemFailedException("Failed to create membership to organization " +
                        name + " for user " + addOrganizationMember.username());
            }
        } catch (NoDataFoundException e) {
            throw new SaveItemFailedException("Failed to create membership to organization " +
                    name + " for user " + addOrganizationMember.username());
        }
    }

    @Permission(action = Action.DELETE, resource = Resource.MEMBERS)
    @Transactional
    public void removeMember(@NotBlank final String name,
                             @NotBlank final String username) {
        LOGGER.info("Remove member {} from organization {}", username, name);
        final var organizationMember = memberRepository.getMember(name, username);

        if (MemberRole.OWNER.name().equals(organizationMember.getRole())) {
            final int numberOfOwners = memberRepository.getMemberCount(organizationMember.getOrganizationId(), MemberRole.OWNER);
            if (numberOfOwners == 1) {
                throw new CannotDeleteItemException("Cannot delete membership of last organization owner");
            }
        }

        final var rowsAffected = memberRepository.removeMember(organizationMember.getOrganizationId(), organizationMember.getUserId());
        if (rowsAffected == 0) {
            throw new SaveItemFailedException("Failed to delete organization " + name);
        }
    }
}
