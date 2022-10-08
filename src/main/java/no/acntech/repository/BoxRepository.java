package no.acntech.repository;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import no.acntech.model.tables.records.BoxesRecord;

import static no.acntech.model.tables.Boxes.BOXES;

@Repository
public class BoxRepository {

    private final DSLContext context;

    public BoxRepository(final DSLContext context) {
        this.context = context;
    }

    public BoxesRecord getBox(final Integer id) {
        try (final var select = context.selectFrom(BOXES)) {
            return select
                    .where(BOXES.ID.eq(id))
                    .fetchSingle();
        }
    }

    public BoxesRecord getBox(final String name,
                              final Integer organizationId) {
        try (final var select = context.selectFrom(BOXES)) {
            return select
                    .where(BOXES.NAME.eq(name))
                    .and(BOXES.ORGANIZATION_ID.eq(organizationId))
                    .fetchSingle();
        }
    }

    public Result<BoxesRecord> findBoxes(final Integer organizationId) {
        try (final var select = context.selectFrom(BOXES)) {
            return select
                    .where(BOXES.ORGANIZATION_ID.eq(organizationId))
                    .fetch();
        }
    }

    @Transactional
    public int createBox(final String name,
                         final String description,
                         final Boolean isPrivate,
                         final Integer organizationId) {
        final Integer downloads = 0;
        try (final var insert = context.insertInto(
                BOXES,
                BOXES.NAME,
                BOXES.DESCRIPTION,
                BOXES.PRIVATE,
                BOXES.DOWNLOADS,
                BOXES.ORGANIZATION_ID,
                BOXES.CREATED)) {
            return insert
                    .values(
                            name,
                            description,
                            isPrivate,
                            downloads,
                            organizationId,
                            LocalDateTime.now())
                    .execute();
        }
    }

    @Transactional
    public int updateBox(final String name,
                         final String description,
                         final Boolean isPrivate,
                         final Integer boxId) {
        try (final var update = context
                .update(BOXES)
                .set(BOXES.NAME, name)
                .set(BOXES.DESCRIPTION, description)
                .set(BOXES.PRIVATE, isPrivate)
                .set(BOXES.MODIFIED, LocalDateTime.now())) {
            return update
                    .where(BOXES.ID.eq(boxId))
                    .execute();
        }
    }

    @Transactional
    public int deleteBox(final Integer boxId) {
        try (final var delete = context.deleteFrom(BOXES)) {
            return delete
                    .where(BOXES.ID.eq(boxId))
                    .execute();
        }
    }

    @Transactional
    public int updateBoxDownload(final Integer downloads,
                                 final Integer boxId) {
        try (final var update = context
                .update(BOXES)
                .set(BOXES.DOWNLOADS, downloads)
                .set(BOXES.MODIFIED, LocalDateTime.now())) {
            return update
                    .where(BOXES.ID.eq(boxId))
                    .execute();
        }
    }
}
