package no.acntech.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

import no.acntech.model.Box;
import no.acntech.model.Group;
import no.acntech.model.ModifyGroup;
import no.acntech.model.Provider;
import no.acntech.model.Version;
import no.acntech.repository.BoxRepository;
import no.acntech.repository.GroupRepository;
import no.acntech.repository.ProviderRepository;
import no.acntech.repository.VersionRepository;

@Service
public class GroupService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GroupService.class);

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

    public Optional<Group> get(final Long groupId) {
        LOGGER.info("Get group with ID {}", groupId);
        return groupRepository.findById(groupId);
    }

    public List<Group> find(final String name) {
        if (name == null) {
            LOGGER.info("Find groups");
            return groupRepository.findAll();
        } else {
            String sanitizedName = name.toLowerCase();
            LOGGER.info("Find groups by name {}", sanitizedName);
            return groupRepository.findByName(sanitizedName);
        }
    }

    @Transactional
    public Group create(final ModifyGroup modifyGroup) {
        String sanitizedName = modifyGroup.getName().toLowerCase();
        LOGGER.info("Create group with name {}", sanitizedName);
        Group group = Group.builder()
                .name(sanitizedName)
                .description(modifyGroup.getDescription())
                .build();

        List<Group> groups = groupRepository.findByName(group.getName());
        if (groups.isEmpty()) {
            return Optional.ofNullable(groupRepository.save(group))
                    .orElseThrow(() -> new IllegalStateException("Save returned no value"));
        } else {
            throw new IllegalStateException("A group with name " + group.getName() + " already exists");
        }
    }

    @SuppressWarnings("Duplicates")
    @Transactional
    public void delete(final Long groupId) {
        LOGGER.info("Delete group with ID {}", groupId);
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

    @Transactional
    public Group update(final Long groupId,
                        @Valid final ModifyGroup modifyGroup) {
        LOGGER.info("Update group with ID {}", groupId);
        Optional<Group> groupOptional = groupRepository.findById(groupId);

        if (groupOptional.isPresent()) {
            String sanitizedName = modifyGroup.getName().toLowerCase();
            Group group = groupOptional.get();
            group.setName(sanitizedName);
            group.setDescription(modifyGroup.getDescription());

            return groupRepository.save(group);
        } else {
            throw new IllegalStateException("No group found for ID " + groupId);
        }
    }
}
