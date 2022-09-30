package no.acntech.service;

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

import no.acntech.exception.CannotSaveItemException;
import no.acntech.exception.ItemNotFoundException;
import no.acntech.exception.SaveItemFailedException;
import no.acntech.filter.StreamFilters;
import no.acntech.model.CreateVersion;
import no.acntech.model.UpdateVersion;
import no.acntech.model.Version;
import no.acntech.model.VersionStatus;
import no.acntech.repository.ProviderRepository;
import no.acntech.repository.VersionRepository;

@Validated
@Service
public class VersionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(VersionService.class);
    private final ConversionService conversionService;
    private final BoxService boxService;
    private final VersionRepository versionRepository;
    private final ProviderRepository providerRepository;

    public VersionService(final ConversionService conversionService,
                          final BoxService boxService,
                          final VersionRepository versionRepository,
                          final ProviderRepository providerRepository) {
        this.conversionService = conversionService;
        this.boxService = boxService;
        this.versionRepository = versionRepository;
        this.providerRepository = providerRepository;
    }

    public Version getVersion(@NotNull final Integer id) {
        LOGGER.info("Get version for ID {}", id);
        try {
            final var record = versionRepository.getVersion(id);
            return conversionService.convert(record, Version.class);
        } catch (NoDataFoundException e) {
            throw new ItemNotFoundException("No version found for ID " + id, e);
        }
    }

    public Version getVersion(@NotBlank final String username,
                              @NotBlank final String name,
                              @NotBlank final String version) {
        final var tag = username + "/" + name;
        LOGGER.info("Get version {} for box {}", version, tag);
        final var box = boxService.getBox(username, name);
        try {
            final var record = versionRepository.getVersion(version, box.id());
            return conversionService.convert(record, Version.class);
        } catch (NoDataFoundException e) {
            throw new ItemNotFoundException("No version " + version + " found for box " + tag, e);
        }
    }

    public List<Version> findVersions(@NotBlank final String username,
                                      @NotBlank final String name) {
        final var tag = username + "/" + name;
        LOGGER.info("Get versions for box {}", tag);
        final var box = boxService.getBox(username, name);
        final var result = versionRepository.findVersions(box.id());
        return result.stream()
                .map(record -> conversionService.convert(record, Version.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public void createVersion(@NotBlank final String username,
                              @NotBlank final String name,
                              @Valid @NotNull final CreateVersion createVersion) {
        final var tag = username + "/" + name;
        LOGGER.info("Create version {} for box {}", createVersion.name(), tag);
        final var box = boxService.getBox(username, name);
        final var rowsAffected = versionRepository.createVersion(
                createVersion.name(),
                createVersion.description(),
                box.id());
        LOGGER.debug("Insert into VERSIONS table affected {} rows", rowsAffected);
        if (rowsAffected == 0) {
            throw new SaveItemFailedException("Failed to create version " + createVersion.name() + " for box " + tag);
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
        final var rowsAffected = versionRepository.updateVersion(
                updateVersion.version(),
                updateVersion.description(),
                version.id());
        LOGGER.debug("Updated record in VERSIONS table affected {} rows", rowsAffected);
        if (rowsAffected == 0) {
            throw new SaveItemFailedException("Failed to update version " + versionParam + " for box " + tag);
        }
    }

    @Transactional
    public void deleteVersion(@NotBlank final String username,
                              @NotBlank final String name,
                              @NotBlank final String versionParam) {
        final var tag = username + "/" + name;
        LOGGER.info("Delete version {} for box {}", versionParam, tag);
        final var version = getVersion(username, name, versionParam);
        final var rowsAffected = versionRepository.deleteVersion(version.id());
        LOGGER.debug("Delete record in VERSIONS table affected {} rows", rowsAffected);
        if (rowsAffected == 0) {
            throw new SaveItemFailedException("Failed to delete version " + versionParam + " for box " + tag);
        }
    }

    @Transactional
    public void updateVersionStatus(@NotBlank final String username,
                                    @NotBlank final String name,
                                    @NotBlank final String versionParam,
                                    @NotNull final VersionStatus versionStatus) {
        final var tag = username + "/" + name;
        LOGGER.info("Update status of version {} for box {}", versionParam, tag);
        final var version = getVersion(username, name, versionParam);
        if (versionStatus == VersionStatus.ACTIVE) {
            final var providers = providerRepository.findProviders(version.id());
            if (providers.isEmpty()) {
                throw new CannotSaveItemException("Version " + versionParam + " for box " + tag + " has no providers");
            }
            final var nonVerifiedProviders = providers.stream()
                    .filter(StreamFilters::hasStatusNonVerified)
                    .count();
            if (nonVerifiedProviders > 0) {
                throw new CannotSaveItemException("Version " + versionParam + " for box " + tag + " has pending providers");
            }
        }
        final var rowsAffected = versionRepository.updateVersionStatus(versionStatus, version.id());
        LOGGER.debug("Updated record in VERSIONS table affected {} rows", rowsAffected);
        if (rowsAffected == 0) {
            throw new SaveItemFailedException("Failed to update status of version " + versionParam + " for box " + tag);
        }
    }

    @Transactional
    public void postDownload(@NotNull final Integer id) {
        final var version = getVersion(id);
        boxService.postDownload(version.boxId());
    }
}
