package no.acntech.repository;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Arrays;

import no.acntech.model.OrganizationRole;
import no.acntech.model.tables.records.MembersRecord;

import static no.acntech.model.tables.Members.MEMBERS;
import static no.acntech.model.tables.Organizations.ORGANIZATIONS;
import static no.acntech.model.tables.Users.USERS;

@Validated
@Repository
public class MemberRepository {

    private final DSLContext context;

    public MemberRepository(final DSLContext context) {
        this.context = context;
    }

    public MembersRecord getMember(@NotBlank final String name,
                                   @NotBlank final String username) {
        try (final var select = context.select(MEMBERS.fields())) {
            try (final var organizationJoin = select.from(MEMBERS)
                    .join(ORGANIZATIONS).on(MEMBERS.ORGANIZATION_ID.eq(ORGANIZATIONS.ID))) {
                try (final var userJoin = organizationJoin
                        .join(USERS).on(MEMBERS.USER_ID.eq(USERS.ID))) {
                    return (MembersRecord) userJoin
                            .where(ORGANIZATIONS.NAME.eq(name))
                            .and(USERS.USERNAME.eq(username))
                            .fetchSingle();
                }
            }
        }
    }

    public int getMemberCount(@NotNull final Integer organizationId,
                              final OrganizationRole... organizationRoles) {
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
    public int addMember(@NotNull final Integer organizationId,
                         @NotNull final Integer userId,
                         @NotNull final OrganizationRole role) {
        try (final var insert = context.insertInto(
                MEMBERS,
                MEMBERS.ORGANIZATION_ID,
                MEMBERS.USER_ID,
                MEMBERS.ROLE)) {
            return insert
                    .values(
                            organizationId,
                            userId,
                            role.name())
                    .execute();
        }
    }

    @Transactional
    public int removeMember(@NotNull final Integer organizationId,
                            @NotNull final Integer userId) {
        try (final var delete = context.deleteFrom(MEMBERS)) {
            return delete
                    .where(MEMBERS.ORGANIZATION_ID.eq(organizationId))
                    .and(MEMBERS.USER_ID.eq(userId))
                    .execute();
        }
    }
}
