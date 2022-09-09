package no.acntech.service;

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
import no.acntech.model.CreateVersion;
import no.acntech.model.UpdateVersion;
import no.acntech.model.Version;
import no.acntech.model.VersionStatus;

import static no.acntech.model.tables.Versions.VERSIONS;

@Validated
@Service
public class VersionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(VersionService.class);
    private final DSLContext context;
    private final ConversionService conversionService;
    private final BoxService boxService;

    public VersionService(final DSLContext context,
                          final ConversionService conversionService,
                          final BoxService boxService) {
        this.context = context;
        this.conversionService = conversionService;
        this.boxService = boxService;
    }

    public Version getVersion(@NotBlank final String username,
                              @NotBlank final String name,
                              @NotBlank final String version) {
        final var tag = username + "/" + name;
        LOGGER.info("Get version {} for box {}", version, tag);
        final var box = boxService.getBox(username, name);
        try (final var select = context.selectFrom(VERSIONS)) {
            final var record = select
                    .where(VERSIONS.VERSION.eq(version))
                    .and(VERSIONS.BOX_ID.eq(box.id()))
                    .fetchSingle();
            return conversionService.convert(record, Version.class);
        } catch (NoDataFoundException e) {
            throw new ItemNotFoundException("No version " + version + " found for box " + tag, e);
        }
    }

    @Transactional
    public void createVersion(@NotBlank final String username,
                              @NotBlank final String name,
                              @Valid @NotNull final CreateVersion createVersion) {
        final var tag = username + "/" + name;
        LOGGER.info("Create version {} for box {}", createVersion.version(), tag);
        final var box = boxService.getBox(username, name);
        try (final var insert = context
                .insertInto(VERSIONS,
                        VERSIONS.VERSION,
                        VERSIONS.DESCRIPTION,
                        VERSIONS.STATUS,
                        VERSIONS.BOX_ID,
                        VERSIONS.CREATED)) {
            final var rowsAffected = insert
                    .values(
                            createVersion.version(),
                            createVersion.description(),
                            VersionStatus.INACTIVE.name(),
                            box.id(),
                            LocalDateTime.now())
                    .execute();
            LOGGER.debug("Insert into VERSIONS table affected {} rows", rowsAffected);
            if (rowsAffected == 0) {
                throw new SaveItemFailedException("Failed to create version " + createVersion.version() +
                        " for box " + tag);
            }
        }
    }

    @Transactional
    public void updateVersion(@NotBlank final String username,
                              @NotBlank final String name,
                              @NotBlank final String versionParam,
                              @Valid @NotNull final UpdateVersion updateVersion) {
        final var tag = username + "/" + name;
        LOGGER.info("Update version {} for box {}", versionParam, tag);
        final var version = getVersion(username, name, versionParam);
        try (final var update = context
                .update(VERSIONS)
                .set(VERSIONS.VERSION, updateVersion.version())
                .set(VERSIONS.DESCRIPTION, updateVersion.description())
                .set(VERSIONS.MODIFIED, LocalDateTime.now())) {
            final var rowsAffected = update
                    .where(VERSIONS.ID.eq(version.id()))
                    .execute();
            LOGGER.debug("Updated record in VERSIONS table affected {} rows", rowsAffected);
            if (rowsAffected == 0) {
                throw new SaveItemFailedException("Failed to update version " + versionParam +
                        " for box " + tag);
            }
        }
    }

    @Transactional
    public void deleteVersion(@NotBlank final String username,
                              @NotBlank final String name,
                              @NotBlank final String versionParam) {
        final var tag = username + "/" + name;
        LOGGER.info("Delete version {} for box {}", versionParam, tag);
        // TODO: Verify that box has no versions
        final var version = getVersion(username, name, versionParam);
        try (final var delete = context.deleteFrom(VERSIONS)) {
            final var rowsAffected = delete
                    .where(VERSIONS.ID.eq(version.id()))
                    .execute();
            LOGGER.debug("Delete record in VERSIONS table affected {} rows", rowsAffected);
            if (rowsAffected == 0) {
                throw new SaveItemFailedException("Failed to delete version " + versionParam +
                        " for box " + tag);
            }
        }
    }

    @Transactional
    public void updateVersionStatus(@NotBlank final String username,
                                    @NotBlank final String name,
                                    @NotBlank final String versionParam,
                                    @NotNull final VersionStatus versionStatus) {
        final var tag = username + "/" + name;
        LOGGER.info("Update status of version {} for box {}", versionParam, tag);
        // TODO: Check if version has providers before activating
        final var version = getVersion(username, name, versionParam);
        try (final var update = context
                .update(VERSIONS)
                .set(VERSIONS.STATUS, versionStatus.name())
                .set(VERSIONS.MODIFIED, LocalDateTime.now())) {
            final var rowsAffected = update
                    .where(VERSIONS.ID.eq(version.id()))
                    .execute();
            LOGGER.debug("Updated record in VERSIONS table affected {} rows", rowsAffected);
            if (rowsAffected == 0) {
                throw new SaveItemFailedException("Failed to update status of version " + versionParam +
                        " for box " + tag);
            }
        }
    }
}
