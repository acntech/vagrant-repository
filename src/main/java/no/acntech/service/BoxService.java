package no.acntech.service;

import org.apache.commons.lang3.tuple.Pair;
import org.jooq.DSLContext;
import org.jooq.exception.NoDataFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import no.acntech.exception.ItemNotFoundException;
import no.acntech.exception.SaveItemFailedException;
import no.acntech.model.Box;
import no.acntech.model.CreateBox;
import no.acntech.model.UpdateBox;

import static no.acntech.model.tables.Boxes.BOXES;

@Validated
@Service
public class BoxService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BoxService.class);
    private final DSLContext context;
    private final ConversionService conversionService;
    private final OrganizationService organizationService;

    public BoxService(final DSLContext context,
                      final ConversionService conversionService,
                      final OrganizationService organizationService) {
        this.context = context;
        this.conversionService = conversionService;
        this.organizationService = organizationService;
    }

    public Box getBox(@NotBlank final String username,
                      @NotBlank final String name) {
        final var tag = username + "/" + name;
        LOGGER.info("Get box {}", tag);
        final var organization = organizationService.getOrganization(username);
        try (final var select = context.selectFrom(BOXES)) {
            final var record = select
                    .where(BOXES.NAME.eq(name))
                    .and(BOXES.ORGANIZATION_ID.eq(organization.id()))
                    .fetchSingle();
            final var pair = Pair.of(record, organization);
            return conversionService.convert(pair, Box.class);
        } catch (NoDataFoundException e) {
            throw new ItemNotFoundException("No box found for tag " + tag, e);
        }
    }

    @Transactional
    public void createBox(@Valid @NotNull final CreateBox createBox) {
        final var tag = createBox.username() + "/" + createBox.name();
        LOGGER.info("Create box {}", tag);
        final var organization = organizationService.getOrganization(createBox.username());
        try (final var insert = context.insertInto(
                BOXES,
                BOXES.NAME,
                BOXES.DESCRIPTION,
                BOXES.ORGANIZATION_ID,
                BOXES.PRIVATE,
                BOXES.DOWNLOADS,
                BOXES.CREATED)) {
            final var rowsAffected = insert
                    .values(
                            createBox.name().toLowerCase(),
                            createBox.description(),
                            organization.id(),
                            createBox.isPrivate(),
                            0,
                            LocalDateTime.now())
                    .execute();
            LOGGER.debug("Insert into BOXES table affected {} rows", rowsAffected);
            if (rowsAffected == 0) {
                throw new SaveItemFailedException("Failed to create box " + tag);
            }
        }
    }

    @Transactional
    public void updateBox(@NotBlank final String username,
                          @NotBlank final String name,
                          @Valid @NotNull final UpdateBox updateBox) {
        final var tag = username + "/" + name;
        LOGGER.info("Update box {}", tag);
        final var box = getBox(username, name);
        try (final var update = context
                .update(BOXES)
                .set(BOXES.NAME, updateBox.name())
                .set(BOXES.DESCRIPTION, updateBox.description())
                .set(BOXES.PRIVATE, updateBox.isPrivate())
                .set(BOXES.MODIFIED, LocalDateTime.now())) {
            final var rowsAffected = update
                    .where(BOXES.ID.eq(box.id()))
                    .execute();
            LOGGER.debug("Updated record in BOXES table affected {} rows", rowsAffected);
            if (rowsAffected == 0) {
                throw new SaveItemFailedException("Failed to update box " + tag);
            }
        }
    }

    @Transactional
    public void deleteBox(@NotBlank final String username,
                          @NotBlank final String name) {
        final var tag = username + "/" + name;
        LOGGER.info("Delete box {}", tag);
        // TODO: Verify that box has no versions
        final var box = getBox(username, name);
        try (final var delete = context.deleteFrom(BOXES)) {
            final var rowsAffected = delete
                    .where(BOXES.ID.eq(box.id()))
                    .execute();
            LOGGER.debug("Delete record in BOXES table affected {} rows", rowsAffected);
            if (rowsAffected == 0) {
                throw new SaveItemFailedException("Failed to delete box " + tag);
            }
        }
    }
}
