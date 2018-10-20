package no.acntech.service.service;

import javax.validation.Valid;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import no.acntech.common.model.Provider;
import no.acntech.common.model.ProviderType;
import no.acntech.common.model.Version;
import no.acntech.service.repository.ProviderRepository;
import no.acntech.service.repository.VersionRepository;

@Service
public class ProviderService {

    private final ProviderRepository providerRepository;
    private final VersionRepository versionRepository;

    public ProviderService(final ProviderRepository providerRepository,
                           final VersionRepository versionRepository) {
        this.providerRepository = providerRepository;
        this.versionRepository = versionRepository;
    }

    public Optional<Provider> get(final Long providerId) {
        return providerRepository.findById(providerId);
    }

    public List<Provider> find(final Long versionId, final ProviderType type) {
        if (versionId == null) {
            throw new IllegalArgumentException("Version ID is null");
        } else {
            if (type == null) {
                return providerRepository.findByVersionId(versionId);
            } else {
                return providerRepository.findByVersionIdAndType(versionId, type);
            }
        }
    }

    @Transactional
    public Provider create(final Long versionId, @Valid final Provider provider) {
        Optional<Version> versionOptional = versionRepository.findById(versionId);
        if (versionOptional.isPresent()) {
            Version version = versionOptional.get();
            List<Provider> providers = providerRepository.findByVersionId(version.getId());
            if (providers.stream().anyMatch(v -> v.getType().equals(provider.getType()))) {
                throw new IllegalStateException("A provider of type " + provider.getType() + " already exists for version " + version.getName());
            } else {
                Provider saveProvider = Provider.builder()
                        .from(provider)
                        .version(version)
                        .build();
                return Optional.ofNullable(providerRepository.save(saveProvider))
                        .orElseThrow(() -> new IllegalStateException("Save returned no value"));
            }
        } else {
            throw new IllegalStateException("A version with ID " + versionId + " does not exist");
        }
    }
}
