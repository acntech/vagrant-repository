package no.acntech.repository;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

import no.acntech.model.MemberRole;
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

    public MembersRecord getMember(final String name, final String username) {
        try (final var select = context.select(MEMBERS.fields())) {
            try (final var organizationJoin = select.from(MEMBERS)
                    .join(ORGANIZATIONS).on(MEMBERS.ORGANIZATION_ID.eq(ORGANIZATIONS.ID))) {
                try (final var userJoin = organizationJoin
                        .join(USERS).on(MEMBERS.USER_ID.eq(USERS.ID))) {
                    final var record = userJoin
                            .where(ORGANIZATIONS.NAME.eq(name))
                            .and(USERS.USERNAME.eq(username))
                            .fetchSingle();
                    return record.into(MembersRecord.class);
                }
            }
        }
    }

    public int getMemberCount(final Integer organizationId,
                              final MemberRole... memberRoles) {
        if (memberRoles == null) {
            try (final var count = context.selectCount()) {
                return count.from(MEMBERS)
                        .where(MEMBERS.ORGANIZATION_ID.eq(organizationId))
                        .execute();
            }
        } else {
            final var roles = Arrays.stream(memberRoles)
                    .map(MemberRole::name).toList();
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
                    final var membersRecords = context.newResult(MEMBERS);
                    membersRecords.addAll(result.into(MembersRecord.class));
                    return membersRecords;
                }
            }
        }
    }

    @Transactional
    public int addMember(final Integer organizationId,
                         final Integer userId,
                         final MemberRole role) {
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
}
