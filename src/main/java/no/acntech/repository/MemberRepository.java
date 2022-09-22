package no.acntech.repository;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

import no.acntech.model.OrganizationRole;
import no.acntech.model.tables.records.MembersRecord;

import static no.acntech.model.tables.Members.MEMBERS;
import static no.acntech.model.tables.Organizations.ORGANIZATIONS;
import static no.acntech.model.tables.Users.USERS;

@Repository
public class MemberRepository {

    private final DSLContext context;

    public MemberRepository(final DSLContext context) {
        this.context = context;
    }

    public MembersRecord getMember(final String name,
                                   final String username) {
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

    public int getMemberCount(final Integer organizationId,
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

    public Result<MembersRecord> findMemberships(final String username) {
        try (final var select = context.select(MEMBERS.fields())) {
            try (final var organizationJoin = select.from(MEMBERS)
                    .join(ORGANIZATIONS).on(MEMBERS.ORGANIZATION_ID.eq(ORGANIZATIONS.ID))) {
                try (final var userJoin = organizationJoin
                        .join(USERS).on(MEMBERS.USER_ID.eq(USERS.ID))) {
                    final var result = userJoin
                            .and(USERS.USERNAME.eq(username))
                            .fetch();
                    return cast(result, MembersRecord.class);
                }
            }
        }
    }

    @Transactional
    public int addMember(final Integer organizationId,
                         final Integer userId,
                         final OrganizationRole role) {
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
    public int removeMember(final Integer organizationId,
                            final Integer userId) {
        try (final var delete = context.deleteFrom(MEMBERS)) {
            return delete
                    .where(MEMBERS.ORGANIZATION_ID.eq(organizationId))
                    .and(MEMBERS.USER_ID.eq(userId))
                    .execute();
        }
    }

    @SuppressWarnings({"unchecked", "unused", "SameParameterValue"})
    private <T extends Record, S extends Result<T>> S cast(Result<?> result, Class<T> clazz) {
        return (S) result;
    }
}
