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
public class GroupService {

    private final GroupRepository groupRepository;
    private final BoxRepository boxRepository;
    private final VersionRepository versionRepository;
    private final ProviderRepository providerRepository;
    private final FileService fileService;

    public GroupService(final GroupRepository groupRepository,
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

    public List<Group> find(final String name) {
        if (name == null) {
            return groupRepository.findAll();
        } else {
            return groupRepository.findByName(name.toLowerCase());
        }
    }

    public Optional<Group> get(final Long groupId) {
        return groupRepository.findById(groupId);
    }

    @Transactional
    public Group create(@Valid final Group group) {
        Group saveGroup = Group.builder()
                .from(group)
                .name(group.getName().toLowerCase())
                .build();

        List<Group> groups = groupRepository.findByName(saveGroup.getName());
        if (groups.isEmpty()) {
            return Optional.ofNullable(groupRepository.save(saveGroup))
                    .orElseThrow(() -> new IllegalStateException("Save returned no value"));
        } else {
            throw new IllegalStateException("A group with name " + saveGroup.getName() + " already exists");
        }
    }

    @SuppressWarnings("Duplicates")
    @Transactional
    public void delete(final Long groupId) {
        Optional<Group> groupOptional = groupRepository.findById(groupId);

        if (groupOptional.isPresent()) {
            Group group = groupOptional.get();

            List<Box> boxes = boxRepository.findByGroupId(groupId);

            boxes.forEach(box -> {
                List<Version> versions = versionRepository.findByBoxId(box.getId());

                versions.forEach(version -> {
                    List<Provider> providers = providerRepository.findByVersionId(version.getId());

                    providers.forEach(provider -> providerRepository.deleteById(provider.getId()));

                    versionRepository.deleteById(version.getId());
                });

                boxRepository.deleteById(box.getId());
            });

            groupRepository.deleteById(groupId);

            fileService.deleteDirectory(
                    group.getName());
        } else {
            throw new IllegalStateException("No group found for ID " + groupId);
        }
    }
}
