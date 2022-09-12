package no.acntech.service;

import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.jooq.exception.NoDataFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

import no.acntech.exception.ChecksumException;
import no.acntech.exception.ItemNotFoundException;
import no.acntech.exception.SaveItemFailedException;
import no.acntech.model.ProviderStatus;
import no.acntech.model.ProviderType;
import no.acntech.model.UpdateStatus;
import no.acntech.model.Upload;

import static no.acntech.model.tables.Uploads.UPLOADS;

@Validated
@Service
public class UploadService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UploadService.class);
    private final DSLContext context;
    private final ConversionService conversionService;
    private final ProviderService providerService;

    public UploadService(final DSLContext context,
                         final ConversionService conversionService,
                         final ProviderService providerService) {
        this.context = context;
        this.conversionService = conversionService;
        this.providerService = providerService;
    }

    public Upload getUpload(@NotBlank final String uid) {
        LOGGER.info("Get upload {}", uid);
        try (final var select = context.selectFrom(UPLOADS)) {
            final var record = select
                    .where(UPLOADS.UID.eq(uid))
                    .and(UPLOADS.STATUS.eq(UpdateStatus.ACTIVE.name()))
                    .fetchSingle();
            return conversionService.convert(record, Upload.class);
        } catch (NoDataFoundException e) {
            throw new ItemNotFoundException("No upload " + uid + " found", e);
        }
    }

    public Upload getUpload(@NotBlank final String username,
                            @NotBlank final String name,
                            @NotBlank final String version,
                            @NotNull final ProviderType providerParam) {
        final var tag = username + "/" + name;
        LOGGER.info("Get upload for provider {} and version {} of box {}", providerParam, version, tag);
        final var provider = providerService.getProvider(username, name, version, providerParam);
        try (final var select = context.selectFrom(UPLOADS)) {
            final var record = select
                    .where(UPLOADS.PROVIDER_ID.eq(provider.id()))
                    .and(UPLOADS.STATUS.eq(UpdateStatus.ACTIVE.name()))
                    .fetchSingle();
            return conversionService.convert(record, Upload.class);
        } catch (NoDataFoundException e) {
            throw new ItemNotFoundException("Failed to get upload for provider " + providerParam +
                    " for version " + version + " of box " + tag, e);
        }
    }

    @Transactional
    public String createUpload(@NotBlank final String username,
                               @NotBlank final String name,
                               @NotBlank final String version,
                               @NotNull final ProviderType providerParam) {
        final var tag = username + "/" + name;
        LOGGER.info("Create upload for provider {} and version {} of box {}", providerParam, version, tag);
        final var uid = UUID.randomUUID().toString();
        final var provider = providerService.getProvider(username, name, version, providerParam);
        try (final var update = context
                .update(UPLOADS)
                .set(UPLOADS.STATUS, UpdateStatus.INACTIVE.name())
                .set(UPLOADS.MODIFIED, LocalDateTime.now())) {
            final var rowsAffected = update
                    .where(UPLOADS.PROVIDER_ID.eq(provider.id()))
                    .and(UPLOADS.STATUS.eq(UpdateStatus.ACTIVE.name()))
                    .execute();
            LOGGER.debug("Updated record in UPLOADS table affected {} rows", rowsAffected);
        }
        try (final var insert = context
                .insertInto(UPLOADS,
                        UPLOADS.UID,
                        UPLOADS.CHECKSUM_TYPE,
                        UPLOADS.STATUS,
                        UPLOADS.PROVIDER_ID,
                        UPLOADS.CREATED)) {
            final var rowsAffected = insert
                    .values(uid,
                            provider.checksumType().name(),
                            UpdateStatus.ACTIVE.name(),
                            provider.id(),
                            LocalDateTime.now())
                    .execute();
            LOGGER.debug("Insert into PROVIDERS table affected {} rows", rowsAffected);
            if (rowsAffected == 0) {
                throw new SaveItemFailedException("Failed to create upload for provider " + providerParam +
                        " for version " + version + " of box " + tag);
            }
            return uid;
        }
    }

    @Transactional
    public void updateUpload(@NotBlank final String uid,
                             @NotNull final Long fileSize,
                             @NotBlank final String checksum) {
        LOGGER.info("Update upload {}", uid);
        final var upload = getUpload(uid);
        final var provider = providerService.getProvider(upload.providerId());
        try (final var update = context
                .update(UPLOADS)
                .set(UPLOADS.FILE_SIZE, fileSize)
                .set(UPLOADS.CHECKSUM, checksum)
                .set(UPLOADS.MODIFIED, LocalDateTime.now())) {
            final var rowsAffected = update
                    .where(UPLOADS.ID.eq(upload.id()))
                    .execute();
            LOGGER.debug("Updated record in UPLOADS table affected {} rows", rowsAffected);
            if (rowsAffected == 0) {
                throw new SaveItemFailedException("Failed to update upload " + uid);
            }
            providerService.updateProviderStatus(provider.id(), ProviderStatus.UPLOADED);
        }
    }

    @Transactional
    public void verifyUpload(@NotBlank final String uid) {
        LOGGER.info("Verify upload {}", uid);
        final var upload = getUpload(uid);
        final var provider = providerService.getProvider(upload.providerId());
        if (StringUtils.isBlank(upload.checksum())) {
            throw new ChecksumException("Upload " + uid + " is missing checksum");
        }
        if (!upload.checksum().equals(provider.checksum())) {
            throw new ChecksumException("Checksum of provider " + provider.name() + " does not match checksum of upload " + upload.uid());
        }
        providerService.updateProviderStatus(provider.id(), ProviderStatus.VERIFIED);
    }
}
