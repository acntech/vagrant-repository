package no.acntech.service.service;

import no.acntech.common.model.*;
import no.acntech.service.repository.BoxRepository;
import no.acntech.service.repository.ProviderRepository;
import no.acntech.service.repository.VersionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Service
public class VersionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(VersionService.class);

    private final BoxRepository boxRepository;
    private final VersionRepository versionRepository;
    private final ProviderRepository providerRepository;
    private final FileService fileService;

    public VersionService(final BoxRepository boxRepository,
                          final VersionRepository versionRepository,
                          final ProviderRepository providerRepository,
                          final FileService fileService) {
        this.boxRepository = boxRepository;
        this.versionRepository = versionRepository;
        this.providerRepository = providerRepository;
        this.fileService = fileService;
    }

    public Optional<Version> get(final Long versionId) {
        LOGGER.info("Get version with ID {}", versionId);
        return versionRepository.findById(versionId);
    }

    public List<Version> find(final Long boxId, final String name) {
        if (name == null) {
            LOGGER.info("Find versions by box-ID {}", boxId);
            return versionRepository.findByBoxId(boxId);
        } else {
            String sanitizedName = name.toLowerCase();
            LOGGER.info("Find versions by box-ID {} and name {}", boxId, sanitizedName);
            return versionRepository.findByBoxIdAndName(boxId, sanitizedName);
        }
    }

    @Transactional
    public Version create(final Long boxId, @Valid final ModifyVersion modifyVersion) {
        String sanitizedName = modifyVersion.getName().toLowerCase();
        LOGGER.info("Create version with name {} for box-ID {}", sanitizedName, boxId);
        Optional<Box> boxOptional = boxRepository.findById(boxId);

        if (boxOptional.isPresent()) {
            Box box = boxOptional.get();
            List<Version> versions = versionRepository.findByBoxId(box.getId());
            Version saveVersion = Version.builder()
                    .name(sanitizedName)
                    .description(modifyVersion.getDescription())
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
        LOGGER.info("Delete version with ID {}", versionId);
        Optional<Version> versionOptional = versionRepository.findById(versionId);

        if (versionOptional.isPresent()) {
            Version version = versionOptional.get();
            Box box = version.getBox();
            Group group = box.getGroup();

            List<Provider> providers = providerRepository.findByVersionId(versionId);

            providers.forEach(provider -> providerRepository.deleteById(provider.getId()));

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
