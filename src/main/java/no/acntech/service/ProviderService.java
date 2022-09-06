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
import no.acntech.model.CreateProvider;
import no.acntech.model.Provider;
import no.acntech.model.ProviderType;
import no.acntech.model.UpdateProvider;

import static no.acntech.model.tables.Providers.PROVIDERS;

@Validated
@Service
public class ProviderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProviderService.class);
    private final DSLContext context;
    private final ConversionService conversionService;
    private final VersionService versionService;

    public ProviderService(final DSLContext context,
                           final ConversionService conversionService,
                           final VersionService versionService) {
        this.context = context;
        this.conversionService = conversionService;
        this.versionService = versionService;
    }


    public Provider getProvider(@NotBlank final String username,
                                @NotBlank final String name,
                                @NotBlank final String versionParam,
                                @NotNull ProviderType provider) {
        final var tag = username + "/" + name;
        LOGGER.info("Get provider {} for version {} of box {}", provider.getProvider(), versionParam, tag);
        final var version = versionService.getVersion(username, name, versionParam);
        try (final var select = context.selectFrom(PROVIDERS)) {
            final var record = select
                    .where(PROVIDERS.NAME.eq(provider.name()))
                    .and(PROVIDERS.VERSION_ID.eq(version.id()))
                    .fetchSingle();
            return conversionService.convert(record, Provider.class);
        } catch (NoDataFoundException e) {
            throw new ItemNotFoundException("No provider " + provider.name() + " found for version " +
                    versionParam + " of box " + tag, e);
        }
    }

    @Transactional
    public void createProvider(@NotBlank final String username,
                               @NotBlank final String name,
                               @NotBlank final String versionParam,
                               @Valid @NotNull final CreateProvider createProvider) {
        final var tag = username + "/" + name;
        LOGGER.info("Create provider {} for version {} of box {}", createProvider.name().getProvider(), versionParam, tag);
        final var version = versionService.getVersion(username, name, versionParam);
        try (final var insert = context
                .insertInto(PROVIDERS,
                        PROVIDERS.NAME,
                        PROVIDERS.HOSTED,
                        PROVIDERS.CHECKSUM,
                        PROVIDERS.CHECKSUM_TYPE,
                        PROVIDERS.VERSION_ID,
                        PROVIDERS.CREATED)) {
            final var rowsAffected = insert
                    .values(
                            createProvider.name().name(),
                            false,
                            createProvider.checksum(),
                            createProvider.checksumType().name(),
                            version.id(),
                            LocalDateTime.now())
                    .execute();
            LOGGER.debug("Insert into PROVIDERS table affected {} rows", rowsAffected);
            if (rowsAffected == 0) {
                throw new SaveItemFailedException("Failed to create provider " + createProvider.name().getProvider() +
                        " for version " + versionParam + " of box " + tag);
            }
        }
    }

    @Transactional
    public void updateProvider(@NotBlank final String username,
                               @NotBlank final String name,
                               @NotBlank final String version,
                               @NotNull ProviderType providerParam,
                               @Valid @NotNull final UpdateProvider updateProvider) {
        final var tag = username + "/" + name;
        LOGGER.info("Update provider {} for version {} of box {}", providerParam.getProvider(), version, tag);
        final var provider = getProvider(username, name, version, providerParam);
        try (final var update = context
                .update(PROVIDERS)
                .set(PROVIDERS.NAME, updateProvider.name().name())
                .set(PROVIDERS.CHECKSUM, updateProvider.checksum())
                .set(PROVIDERS.CHECKSUM_TYPE, updateProvider.checksumType().name())) {
            final var rowsAffected = update
                    .where(PROVIDERS.ID.eq(provider.id()))
                    .execute();
            LOGGER.debug("Updated record in PROVIDERS table affected {} rows", rowsAffected);
            if (rowsAffected == 0) {
                throw new SaveItemFailedException("Failed to update provider " + providerParam.getProvider() +
                        " for version " + version + " of box " + tag);
            }
        }
    }

    @Transactional
    public void deleteProvider(@NotBlank final String username,
                               @NotBlank final String name,
                               @NotBlank final String version,
                               @NotNull ProviderType providerParam) {
        final var tag = username + "/" + name;
        LOGGER.info("Delete provider {} for version {} of box {}", providerParam.getProvider(), version, tag);
        final var provider = getProvider(username, name, version, providerParam);
        try (final var delete = context.deleteFrom(PROVIDERS)) {
            final var rowsAffected = delete
                    .where(PROVIDERS.ID.eq(provider.id()))
                    .execute();
            LOGGER.debug("Delete record in PROVIDERS table affected {} rows", rowsAffected);
            if (rowsAffected == 0) {
                throw new SaveItemFailedException("Failed to delete provider " + providerParam.getProvider() +
                        " for version " + version + " of box " + tag);
            }
        }
    }
}
