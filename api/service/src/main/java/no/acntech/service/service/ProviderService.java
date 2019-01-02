package no.acntech.service.service;

import no.acntech.common.model.*;
import no.acntech.service.repository.ProviderRepository;
import no.acntech.service.repository.VersionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProviderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProviderService.class);

    private final VersionRepository versionRepository;
    private final ProviderRepository providerRepository;
    private final FileService fileService;

    public ProviderService(final VersionRepository versionRepository,
                           final ProviderRepository providerRepository,
                           final FileService fileService) {
        this.versionRepository = versionRepository;
        this.providerRepository = providerRepository;
        this.fileService = fileService;
    }

    public Optional<Provider> get(final Long providerId) {
        LOGGER.info("Get provider with ID {}", providerId);
        return providerRepository.findById(providerId);
    }

    public List<Provider> find(final Long versionId, final ProviderType providerType) {
        if (providerType == null) {
            LOGGER.info("Find providers by version-ID {}", versionId);
            return providerRepository.findByVersionId(versionId);
        } else {
            LOGGER.info("Find providers by version-ID {} and name {}", versionId, providerType);
            return providerRepository.findByVersionIdAndProviderType(versionId, providerType);
        }
    }

    @Transactional
    public Provider create(final Long versionId, final ModifyProvider modifyProvider) {
        LOGGER.info("Create provider with type {} for version-ID {}", modifyProvider.getProviderType(), versionId);
        Optional<Version> versionOptional = versionRepository.findById(versionId);

        if (versionOptional.isPresent()) {
            Version version = versionOptional.get();
            List<Provider> providers = providerRepository.findByVersionId(version.getId());
            Provider saveProvider = Provider.builder()
                    .providerType(modifyProvider.getProviderType())
                    .version(version)
                    .build();

            if (providers.stream().anyMatch(v -> v.getProviderType().equals(saveProvider.getProviderType()))) {
                throw new IllegalStateException("A provider of type " + saveProvider.getProviderType() + " already exists for version " + version.getName());
            } else {
                return Optional.of(providerRepository.save(saveProvider))
                        .orElseThrow(() -> new IllegalStateException("Save returned no value"));
            }
        } else {
            throw new IllegalStateException("A version with ID " + versionId + " does not exist");
        }
    }

    @Transactional
    public Provider update(final Long providerId,
                           final MultipartFile file) {
        LOGGER.info("Update provider with ID {}", providerId);
        Optional<Provider> providerOptional = providerRepository.findById(providerId);

        if (providerOptional.isPresent()) {
            Provider provider = providerOptional.get();
            Version version = provider.getVersion();
            Box box = version.getBox();
            Group group = box.getGroup();

            ProviderFile providerFile = fileService.saveFile(
                    group.getName(),
                    box.getName(),
                    version.getName(),
                    provider.getProviderType(),
                    file);

            provider.setSize(providerFile.getFileSize());
            provider.setChecksumType(providerFile.getChecksumType());
            provider.setChecksum(providerFile.getChecksum());

            return providerRepository.save(provider);
        } else {
            throw new IllegalStateException("No provider found for ID " + providerId);
        }
    }

    @Transactional
    public void delete(final Long providerId) {
        LOGGER.info("Delete provider with ID {}", providerId);
        Optional<Provider> providerOptional = providerRepository.findById(providerId);

        if (providerOptional.isPresent()) {
            Provider provider = providerOptional.get();
            Version version = provider.getVersion();
            Box box = version.getBox();
            Group group = box.getGroup();

            providerRepository.deleteById(provider.getId());

            fileService.deleteDirectory(
                    group.getName(),
                    box.getName(),
                    version.getName(),
                    provider.getProviderType());
        } else {
            throw new IllegalStateException("No provider found for ID " + providerId);
        }
    }

    public List<ProviderFile> findFiles(final Long providerId) {
        LOGGER.info("Get provider with ID {}", providerId);
        Optional<Provider> providerOptional = providerRepository.findById(providerId);

        if (providerOptional.isPresent()) {
            Provider provider = providerOptional.get();
            Version version = provider.getVersion();
            Box box = version.getBox();
            Group group = box.getGroup();

            List<ProviderFile> providerFiles = fileService.findDirectoryFiles(
                    group.getName(),
                    box.getName(),
                    version.getName(),
                    provider.getProviderType());
            return providerFiles.stream()
                    .peek(providerFile -> providerFile.setProviderId(providerId))
                    .collect(Collectors.toList());
        } else {
            throw new IllegalStateException("No provider found for ID " + providerId);
        }
    }
}
