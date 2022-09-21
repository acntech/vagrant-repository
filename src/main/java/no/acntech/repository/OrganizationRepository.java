package no.acntech.repository;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import no.acntech.model.tables.records.OrganizationsRecord;

import static no.acntech.model.tables.Organizations.ORGANIZATIONS;

@Repository
public class OrganizationRepository {

    private final DSLContext context;

    public OrganizationRepository(final DSLContext context) {
        this.context = context;
    }

    public OrganizationsRecord getOrganization(final Integer id) {
        try (final var select = context.selectFrom(ORGANIZATIONS)) {
            return select
                    .where(ORGANIZATIONS.ID.eq(id))
                    .fetchSingle();
        }
    }

    public OrganizationsRecord getOrganization(final String name) {
        try (final var select = context.selectFrom(ORGANIZATIONS)) {
            return select
                    .where(ORGANIZATIONS.NAME.eq(name))
                    .fetchSingle();
        }
    }

    @Transactional
    public int createOrganization(final String name,
                                  final String description) {
        try (final var insert = context.insertInto(
                ORGANIZATIONS,
                ORGANIZATIONS.NAME,
                ORGANIZATIONS.DESCRIPTION,
                ORGANIZATIONS.CREATED)) {
            return insert
                    .values(
                            name,
                            description,
                            LocalDateTime.now())
                    .execute();
        }
    }

    @Transactional
    public int updateOrganization(final String oldName,
                                  final String newName,
                                  final String description) {
        try (final var update = context
                .update(ORGANIZATIONS)
                .set(ORGANIZATIONS.NAME, newName)
                .set(ORGANIZATIONS.DESCRIPTION, description)
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
