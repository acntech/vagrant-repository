package no.acntech.service.service;

import no.acntech.common.model.*;
import no.acntech.service.repository.ProviderRepository;
import no.acntech.service.repository.VersionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Service
public class ProviderService {

    private final ProviderRepository providerRepository;
    private final VersionRepository versionRepository;
    private final FileService fileService;

    public ProviderService(final ProviderRepository providerRepository,
                           final VersionRepository versionRepository,
                           final FileService fileService) {
        this.providerRepository = providerRepository;
        this.versionRepository = versionRepository;
        this.fileService = fileService;
    }

    public Optional<Provider> get(final Long providerId) {
        return providerRepository.findById(providerId);
    }

    public List<Provider> find(final Long versionId, final ProviderType providerType) {
        if (versionId == null) {
            throw new IllegalArgumentException("Version ID is null");
        } else {
            if (providerType == null) {
                return providerRepository.findByVersionId(versionId);
            } else {
                return providerRepository.findByVersionIdAndProviderType(versionId, providerType);
            }
        }
    }

    @Transactional
    public Provider create(final Long versionId, @Valid final Provider provider) {
        Optional<Version> versionOptional = versionRepository.findById(versionId);

        if (versionOptional.isPresent()) {
            Version version = versionOptional.get();
            List<Provider> providers = providerRepository.findByVersionId(version.getId());
            Provider saveProvider = Provider.builder()
                    .from(provider)
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
    public Optional<Provider> update(final Long providerId,
                                     final MultipartFile file) {
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

            return Optional.of(providerRepository.save(provider));
        } else {
            throw new IllegalStateException("No provider found for ID " + providerId);
        }
    }

    @Transactional
    public void delete(final Long providerId) {
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
}
