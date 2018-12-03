package no.acntech.service.service;

import no.acntech.common.model.Box;
import no.acntech.common.model.Group;
import no.acntech.common.model.Provider;
import no.acntech.common.model.Version;
import no.acntech.service.repository.BoxRepository;
import no.acntech.service.repository.GroupRepository;
import no.acntech.service.repository.ProviderRepository;
import no.acntech.service.repository.VersionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Service
public class BoxService {


    private final GroupRepository groupRepository;
    private final BoxRepository boxRepository;
    private final VersionRepository versionRepository;
    private final ProviderRepository providerRepository;
    private final FileService fileService;

    public BoxService(final GroupRepository groupRepository,
                      final BoxRepository boxRepository,
                      final VersionRepository versionRepository,
                      final ProviderRepository providerRepository,
                      final FileService fileService) {
        this.groupRepository = groupRepository;
        this.boxRepository = boxRepository;
        this.versionRepository = versionRepository;
        this.providerRepository = providerRepository;
        this.fileService = fileService;
    }

    public Optional<Box> get(final Long boxId) {
        return boxRepository.findById(boxId);
    }

    public List<Box> find(final Long groupId, final String name) {
        if (groupId == null) {
            throw new IllegalArgumentException("Group ID is null");
        } else {
            if (name == null) {
                return boxRepository.findByGroupId(groupId);
            } else {
                return boxRepository.findByGroupIdAndName(groupId, name.toLowerCase());
            }
        }
    }

    @Transactional
    public Box create(final Long groupId, @Valid final Box box) {
        Optional<Group> groupOptional = groupRepository.findById(groupId);

        if (groupOptional.isPresent()) {
            Group group = groupOptional.get();
            List<Box> boxes = boxRepository.findByGroupId(group.getId());
            Box saveBox = Box.builder()
                    .from(box)
                    .name(box.getName().toLowerCase())
                    .group(group)
                    .build();

            if (boxes.stream().anyMatch(b -> b.getName().equals(saveBox.getName()))) {
                throw new IllegalStateException("A box with name " + saveBox.getName() + " already exists for group " + group.getName());
            } else {
                return Optional.ofNullable(boxRepository.save(saveBox))
                        .orElseThrow(() -> new IllegalStateException("Save returned no value"));
            }
        } else {
            throw new IllegalStateException("A group with ID " + groupId + " does not exist");
        }
    }

    @SuppressWarnings("Duplicates")
    @Transactional
    public void delete(final Long boxId) {
        Optional<Box> boxOptional = boxRepository.findById(boxId);

        if (boxOptional.isPresent()) {
            Box box = boxOptional.get();
            Group group = box.getGroup();

            List<Version> versions = versionRepository.findByBoxId(boxId);

            versions.forEach(version -> {
                List<Provider> providers = providerRepository.findByVersionId(version.getId());

                providers.forEach(provider -> providerRepository.deleteById(provider.getId()));

                versionRepository.deleteById(version.getId());
            });

            boxRepository.deleteById(boxId);

            fileService.deleteDirectory(
                    group.getName(),
                    box.getName());
        } else {
            throw new IllegalStateException("No box found for ID " + boxId);
        }
    }
}
