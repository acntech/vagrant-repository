package no.acntech.service;

import org.jooq.DSLContext;
import org.jooq.exception.NoDataFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Arrays;

import no.acntech.exception.ItemNotFoundException;
import no.acntech.model.OrganizationMember;
import no.acntech.model.OrganizationRole;

import static no.acntech.model.tables.Members.MEMBERS;
import static no.acntech.model.tables.Organizations.ORGANIZATIONS;
import static no.acntech.model.tables.Users.USERS;

@Validated
@Service
public class OrganizationMemberService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationMemberService.class);
    private final DSLContext context;
    private final ConversionService conversionService;

    public OrganizationMemberService(final DSLContext context,
                                     final ConversionService conversionService) {
        this.context = context;
        this.conversionService = conversionService;
    }

    public OrganizationMember getMember(@NotBlank final String name,
                                        @NotBlank final String username) {
        try (final var select = context.select(MEMBERS.fields())) {
            try (final var organizationJoin = select.from(MEMBERS)
                    .join(ORGANIZATIONS).on(MEMBERS.ORGANIZATION_ID.eq(ORGANIZATIONS.ID))) {
                try (final var userJoin = organizationJoin
                        .join(USERS).on(MEMBERS.USER_ID.eq(USERS.ID))) {
                    final var record = userJoin
                            .where(ORGANIZATIONS.NAME.eq(name))
                            .and(USERS.USERNAME.eq(username))
                            .fetchSingle();
                    return conversionService.convert(record, OrganizationMember.class);
                }
            }
        } catch (NoDataFoundException e) {
            throw new ItemNotFoundException("No membership found to organization with name " + name +
                    " for user with username " + username, e);
        }
    }

    public int getMemberCount(@NotNull Integer organizationId,
                              OrganizationRole... organizationRoles) {
        if (organizationRoles == null) {
            try (final var count = context.selectCount()) {
                return count.from(MEMBERS)
                        .where(MEMBERS.ORGANIZATION_ID.eq(organizationId))
                        .execute();
            }
        } else {
            final var roles = Arrays.stream(organizationRoles)
                    .map(OrganizationRole::name).toList();
            try (final var count = context.selectCount()) {
                return count.from(MEMBERS)
                        .where(MEMBERS.ORGANIZATION_ID.eq(organizationId))
                        .and(MEMBERS.ROLE.in(roles))
                        .execute();
            }
        }
    }

    @Transactional
    public int addMember(@NotNull Integer organizationId,
                         @NotNull Integer userId,
                         @NotNull OrganizationRole role) {
        try (final var insert = context.insertInto(
                MEMBERS,
                MEMBERS.ORGANIZATION_ID,
                MEMBERS.USER_ID,
                MEMBERS.ROLE)) {
            final var rowsAffected = insert
                    .values(
                            organizationId,
                            userId,
                            role.name())
                    .execute();
            LOGGER.debug("Insert into MEMBERS table affected {} rows", rowsAffected);
            return rowsAffected;
        }
    }

    @Transactional
    public int removeMember(@NotNull Integer organizationId,
                            @NotNull Integer userId) {
        try (final var delete = context.deleteFrom(MEMBERS)) {
            final var rowsAffected = delete
                    .where(MEMBERS.ORGANIZATION_ID.eq(organizationId))
                    .and(MEMBERS.USER_ID.eq(userId))
                    .execute();
            LOGGER.debug("Delete record in MEMBERS table affected {} rows", rowsAffected);
            return rowsAffected;
        }
    }
}
