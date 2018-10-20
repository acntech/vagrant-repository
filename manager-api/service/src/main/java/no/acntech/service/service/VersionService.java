package no.acntech.service.service;

import javax.validation.Valid;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import no.acntech.common.model.Box;
import no.acntech.common.model.Version;
import no.acntech.service.repository.BoxRepository;
import no.acntech.service.repository.VersionRepository;

@Service
public class VersionService {

    private final VersionRepository versionRepository;
    private final BoxRepository boxRepository;

    public VersionService(final VersionRepository versionRepository,
                          final BoxRepository boxRepository) {
        this.versionRepository = versionRepository;
        this.boxRepository = boxRepository;
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
                return versionRepository.findByBoxIdAndName(boxId, name);
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
}
