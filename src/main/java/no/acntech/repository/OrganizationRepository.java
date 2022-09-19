package no.acntech.repository;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import no.acntech.model.tables.records.OrganizationsRecord;

import static no.acntech.model.tables.Organizations.ORGANIZATIONS;

@Validated
@Repository
public class OrganizationRepository {

    private final DSLContext context;

    public OrganizationRepository(final DSLContext context) {
        this.context = context;
    }

    public OrganizationsRecord getOrganization(@NotNull final Integer id) {
        try (final var select = context.selectFrom(ORGANIZATIONS)) {
            return select
                    .where(ORGANIZATIONS.ID.eq(id))
                    .fetchSingle();
        }
    }

    public OrganizationsRecord getOrganization(@NotBlank final String name) {
        try (final var select = context.selectFrom(ORGANIZATIONS)) {
            return select
                    .where(ORGANIZATIONS.NAME.eq(name))
                    .fetchSingle();
        }
    }

    @Transactional
    public int createOrganization(@NotBlank final String name,
                                  @NotBlank final String description) {
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
    public int updateOrganization(@NotBlank final String oldName,
                                  @NotBlank final String newName,
                                  @NotBlank final String description) {
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
    public int deleteOrganization(@NotBlank final String name) {
        try (final var delete = context.deleteFrom(ORGANIZATIONS)) {
            return delete
                    .where(ORGANIZATIONS.NAME.eq(name))
                    .execute();
        }
    }
}
