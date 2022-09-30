package no.acntech.repository;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import no.acntech.model.VersionStatus;
import no.acntech.model.tables.records.VersionsRecord;

import static no.acntech.model.tables.Versions.VERSIONS;

@Repository
public class VersionRepository {

    private final DSLContext context;

    public VersionRepository(final DSLContext context) {
        this.context = context;
    }

    public VersionsRecord getVersion(final Integer id) {
        try (final var select = context.selectFrom(VERSIONS)) {
            return select
                    .where(VERSIONS.ID.eq(id))
                    .fetchSingle();
        }
    }

    public VersionsRecord getVersion(final String version,
                                     final Integer boxId) {
        try (final var select = context.selectFrom(VERSIONS)) {
            return select
                    .where(VERSIONS.NAME.eq(version))
                    .and(VERSIONS.BOX_ID.eq(boxId))
                    .fetchSingle();
        }
    }

    public Result<VersionsRecord> findVersions(final Integer boxId) {
        try (final var select = context.selectFrom(VERSIONS)) {
            return select
                    .where(VERSIONS.BOX_ID.eq(boxId))
                    .fetch();
        }
    }

    @Transactional
    public int createVersion(final String name,
                             final String description,
                             final Integer boxId) {
        try (final var insert = context
                .insertInto(VERSIONS,
                        VERSIONS.NAME,
                        VERSIONS.DESCRIPTION,
                        VERSIONS.STATUS,
                        VERSIONS.BOX_ID,
                        VERSIONS.CREATED)) {
            return insert
                    .values(
                            name,
                            description,
                            VersionStatus.INACTIVE.name(),
                            boxId,
                            LocalDateTime.now())
                    .execute();
        }
    }

    @Transactional
    public int updateVersion(final String name,
                             final String description,
                             final Integer versionId) {
        try (final var update = context
                .update(VERSIONS)
                .set(VERSIONS.NAME, name)
                .set(VERSIONS.DESCRIPTION, description)
                .set(VERSIONS.MODIFIED, LocalDateTime.now())) {
            return update
                    .where(VERSIONS.ID.eq(versionId))
                    .execute();
        }
    }

    @Transactional
    public int deleteVersion(final Integer versionId) {
        try (final var delete = context.deleteFrom(VERSIONS)) {
            return delete
                    .where(VERSIONS.ID.eq(versionId))
                    .execute();
        }
    }

    @Transactional
    public int updateVersionStatus(final VersionStatus versionStatus,
                                   final Integer versionId) {
        try (final var update = context
                .update(VERSIONS)
                .set(VERSIONS.STATUS, versionStatus.name())
                .set(VERSIONS.MODIFIED, LocalDateTime.now())) {
            return update
                    .where(VERSIONS.ID.eq(versionId))
                    .execute();
        }
    }
}
