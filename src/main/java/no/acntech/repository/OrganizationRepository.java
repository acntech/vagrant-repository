package no.acntech.repository;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import no.acntech.model.tables.records.OrganizationsRecord;

import static no.acntech.model.tables.Members.MEMBERS;
import static no.acntech.model.tables.Organizations.ORGANIZATIONS;
import static no.acntech.model.tables.Users.USERS;

@Repository
public class OrganizationRepository {

    private final DSLContext context;

    public OrganizationRepository(final DSLContext context) {
        this.context = context;
    }

    public OrganizationsRecord getOrganization(final String name) {
        try (final var select = context.selectFrom(ORGANIZATIONS)) {
            return select
                    .where(ORGANIZATIONS.NAME.eq(name))
                    .fetchSingle();
        }
    }

    public Result<OrganizationsRecord> findOrganizationsForUser(final String username) {
        try (final var select = context.select(ORGANIZATIONS.fields())) {
            try (final var organizationJoin = select.from(ORGANIZATIONS)
                    .join(MEMBERS).on(ORGANIZATIONS.ID.eq(MEMBERS.ORGANIZATION_ID))) {
                try (final var userJoin = organizationJoin
                        .join(USERS).on(MEMBERS.USER_ID.eq(USERS.ID))) {
                    final var result = userJoin
                            .where(USERS.USERNAME.eq(username))
                            .fetch();
                    final var organizationsRecords = context.newResult(ORGANIZATIONS);
                    organizationsRecords.addAll(result.into(OrganizationsRecord.class));
                    return organizationsRecords;
                }
            }
        }
    }

    @Transactional
    public int createOrganization(final String name,
                                  final String description,
                                  final Boolean isPrivate) {
        try (final var insert = context.insertInto(
                ORGANIZATIONS,
                ORGANIZATIONS.NAME,
                ORGANIZATIONS.DESCRIPTION,
                ORGANIZATIONS.PRIVATE,
                ORGANIZATIONS.CREATED)) {
            return insert
                    .values(
                            name,
                            description,
                            isPrivate,
                            LocalDateTime.now())
                    .execute();
        }
    }

    @Transactional
    public int updateOrganization(final String oldName,
                                  final String newName,
                                  final String description,
                                  final Boolean isPrivate) {
        try (final var update = context
                .update(ORGANIZATIONS)
                .set(ORGANIZATIONS.NAME, newName)
                .set(ORGANIZATIONS.DESCRIPTION, description)
                .set(ORGANIZATIONS.PRIVATE, isPrivate)
                .set(ORGANIZATIONS.MODIFIED, LocalDateTime.now())) {
            return update
                    .where(ORGANIZATIONS.NAME.eq(oldName))
                    .execute();
        }
    }

    @Transactional
    public int deleteOrganization(final String name) {
        try (final var delete = context.deleteFrom(ORGANIZATIONS)) {
            return delete
                    .where(ORGANIZATIONS.NAME.eq(name))
                    .execute();
        }
    }
}
