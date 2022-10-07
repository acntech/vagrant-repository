package no.acntech.service;

import org.apache.commons.lang3.StringUtils;
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
import java.util.List;
import java.util.stream.Collectors;

import no.acntech.annotation.Permission;
import no.acntech.exception.ItemNotFoundException;
import no.acntech.exception.SaveItemFailedException;
import no.acntech.model.Action;
import no.acntech.model.CreateProvider;
import no.acntech.model.Provider;
import no.acntech.model.ProviderStatus;
import no.acntech.model.ProviderType;
import no.acntech.model.Resource;
import no.acntech.model.UpdateProvider;
import no.acntech.repository.ProviderRepository;

@Validated
@Service
public class ProviderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProviderService.class);
    private final ConversionService conversionService;
    private final VersionService versionService;
    private final ProviderRepository providerRepository;

    public ProviderService(final ConversionService conversionService,
                           final VersionService versionService,
                           final ProviderRepository providerRepository) {
        this.conversionService = conversionService;
        this.versionService = versionService;
        this.providerRepository = providerRepository;
    }

    // Only use internally!!
    public Provider getProvider(@NotNull final Integer id) {
        LOGGER.info("Get provider for ID {}", id);
        try {
            final var record = providerRepository.getProvider(id);
            return conversionService.convert(record, Provider.class);
        } catch (NoDataFoundException e) {
            throw new ItemNotFoundException("No provider found for ID " + id, e);
        }
    }

    @Permission(action = Action.READ, resource = Resource.PROVIDERS)
    public Provider getProvider(@NotBlank final String username,
                                @NotBlank final String name,
                                @NotBlank final String versionParam,
                                @NotNull final ProviderType provider) {
        final var tag = username + "/" + name;
        LOGGER.info("Get provider {} for version {} of box {}", provider, versionParam, tag);
        final var version = versionService.getVersion(username, name, versionParam);
        try {
            final var record = providerRepository.getProvider(provider, version.id());
            return conversionService.convert(record, Provider.class);
        } catch (NoDataFoundException e) {
            throw new ItemNotFoundException("No provider " + provider.name() + " found for version " +
                    versionParam + " of box " + tag, e);
        }
    }

    @Permission(action = Action.READ, resource = Resource.PROVIDERS)
    public List<Provider> findProviders(@NotBlank final String username,
                                        @NotBlank final String name,
                                        @NotBlank final String versionParam) {
        final var tag = username + "/" + name;
        LOGGER.info("Find providers for version {} of box {}", versionParam, tag);
        final var version = versionService.getVersion(username, name, versionParam);
        final var result = providerRepository.findProviders(version.id());
        return result.stream()
                .map(record -> conversionService.convert(record, Provider.class))
                .collect(Collectors.toList());
    }

    @Permission(action = Action.CREATE, resource = Resource.PROVIDERS)
    @Transactional
    public void createProvider(@NotBlank final String username,
                               @NotBlank final String name,
                               @NotBlank final String versionParam,
                               @Valid @NotNull final CreateProvider createProvider) {
        final var tag = username + "/" + name;
        LOGGER.info("Create provider {} for version {} of box {}", createProvider.name(), versionParam, tag);
        final var version = versionService.getVersion(username, name, versionParam);
        final var status = StringUtils.isBlank(createProvider.url()) ? ProviderStatus.PENDING : ProviderStatus.EXTERNAL;
        final var rowsAffected = providerRepository.createProvider(
                createProvider.name(),
                createProvider.checksum().toLowerCase(),
                createProvider.checksumType(),
                createProvider.url(),
                status,
                version.id());
        LOGGER.debug("Insert into PROVIDERS table affected {} rows", rowsAffected);
        if (rowsAffected == 0) {
            throw new SaveItemFailedException("Failed to create provider " + createProvider.name() +
                    " for version " + versionParam + " of box " + tag);
        }
    }

    @Permission(action = Action.UPDATE, resource = Resource.PROVIDERS)
    @Transactional
    public void updateProvider(@NotBlank final String username,
                               @NotBlank final String name,
                               @NotBlank final String version,
                               @NotNull final ProviderType providerParam,
                               @Valid @NotNull final UpdateProvider updateProvider) {
        final var tag = username + "/" + name;
        LOGGER.info("Update provider {} for version {} of box {}", providerParam, version, tag);
        final var provider = getProvider(username, name, version, providerParam);
        final var status = StringUtils.isBlank(updateProvider.url()) ? ProviderStatus.PENDING : ProviderStatus.EXTERNAL;
        final var rowsAffected = providerRepository.updateProvider(
                updateProvider.name(),
                updateProvider.checksum(),
                updateProvider.checksumType(),
                updateProvider.url(),
                status,
                provider.id()
        );
        LOGGER.debug("Updated record in PROVIDERS table affected {} rows", rowsAffected);
        if (rowsAffected == 0) {
            throw new SaveItemFailedException("Failed to update provider " + providerParam +
                    " for version " + version + " of box " + tag);
        }
    }

    @Permission(action = Action.DELETE, resource = Resource.PROVIDERS)
    @Transactional
    public void deleteProvider(@NotBlank final String username,
                               @NotBlank final String name,
                               @NotBlank final String version,
                               @NotNull final ProviderType providerParam) {
        final var tag = username + "/" + name;
        LOGGER.info("Delete provider {} for version {} of box {}", providerParam, version, tag);
        final var provider = getProvider(username, name, version, providerParam);
        final var rowsAffected = providerRepository.deleteProvider(provider.id());
        LOGGER.debug("Delete record in PROVIDERS table affected {} rows", rowsAffected);
        if (rowsAffected == 0) {
            throw new SaveItemFailedException("Failed to delete provider " + providerParam +
                    " for version " + version + " of box " + tag);
        }
    }

    @Transactional
    public void updateProviderStatus(@NotNull final Integer id,
                                     @NotNull final ProviderStatus status) {
        LOGGER.info("Update provider for ID {}", id);
        final var rowsAffected = providerRepository.updateProviderStatus(id, status);
        LOGGER.debug("Updated record in PROVIDERS table affected {} rows", rowsAffected);
        if (rowsAffected == 0) {
            throw new SaveItemFailedException("Failed to update provider with ID " + id);
        }
    }

    @Transactional
    public void postDownload(@NotNull final Integer id) {
        try {
            final var record = providerRepository.getProvider(id);
            versionService.postDownload(record.getVersionId());
        } catch (NoDataFoundException e) {
            throw new ItemNotFoundException("No provider found for ID " + id, e);
        }
    }
}
