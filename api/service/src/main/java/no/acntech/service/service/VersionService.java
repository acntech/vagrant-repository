package no.acntech.service.service;

import no.acntech.common.model.Box;
import no.acntech.common.model.Group;
import no.acntech.common.model.Provider;
import no.acntech.common.model.Version;
import no.acntech.service.repository.BoxRepository;
import no.acntech.service.repository.ProviderRepository;
import no.acntech.service.repository.VersionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Service
public class VersionService {

    private final ProviderRepository providerRepository;
    private final VersionRepository versionRepository;
    private final BoxRepository boxRepository;
    private final FileService fileService;

    public VersionService(final ProviderRepository providerRepository,
                          final VersionRepository versionRepository,
                          final BoxRepository boxRepository,
                          final FileService fileService) {
        this.providerRepository = providerRepository;
        this.versionRepository = versionRepository;
        this.boxRepository = boxRepository;
        this.fileService = fileService;
    }

    public Optional<Version> get(final Long versionId) {
        return versionRepository.findById(versionId);
    }

    public List<Version> find(final Long boxId, final String name) {
        if (boxId == null) {
            throw new IllegalArgumentException("Version ID is null");
        } else {
            if (name == null) {
                return versionRepository.findByBoxId(boxId);
            } else {
                return versionRepository.findByBoxIdAndName(boxId, name.toLowerCase());
            }
        }
    }

    @Transactional
    public Version create(final Long boxId, @Valid final Version version) {
        Optional<Box> boxOptional = boxRepository.findById(boxId);

        if (boxOptional.isPresent()) {
            Box box = boxOptional.get();
            List<Version> versions = versionRepository.findByBoxId(box.getId());
            Version saveVersion = Version.builder()
                    .from(version)
                    .name(version.getName().toLowerCase())
                    .box(box)
                    .build();

            if (versions.stream().anyMatch(v -> v.getName().equals(saveVersion.getName()))) {
                throw new IllegalStateException("A version with name " + saveVersion.getName() + " already exists for box " + box.getName());
            } else {
                return Optional.ofNullable(versionRepository.save(saveVersion))
                        .orElseThrow(() -> new IllegalStateException("Save returned no value"));
            }
        } else {
            throw new IllegalStateException("A box with ID " + boxId + " does not exist");
        }
    }

    @Transactional
    public void delete(final Long versionId) {
        Optional<Version> versionOptional = versionRepository.findById(versionId);

        if (versionOptional.isPresent()) {
            Version version = versionOptional.get();
            Box box = version.getBox();
            Group group = box.getGroup();

            List<Provider> providers = providerRepository.findByVersionId(versionId);

            providers.forEach(provider -> {
                providerRepository.deleteById(provider.getId());
            });

            versionRepository.deleteById(versionId);

            fileService.deleteDirectory(
                    group.getName(),
                    box.getName(),
                    version.getName());
        } else {
            throw new IllegalStateException("No version found for ID " + versionId);
        }
    }
}
