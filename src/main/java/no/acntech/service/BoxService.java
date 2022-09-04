package no.acntech.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import no.acntech.model.Box;
import no.acntech.model.ModifyBox;

@Service
public class BoxService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BoxService.class);

    private final FileService fileService;

    public BoxService(final FileService fileService) {
        this.fileService = fileService;
    }

    public Optional<Box> get(final Long boxId) {
        LOGGER.info("Get box with ID {}", boxId);
        return null;
    }

    public List<Box> find(final Long groupId, final String name) {
        /*if (name == null) {
            LOGGER.info("Find boxes by group-ID {}", groupId);
            return boxRepository.findByGroupId(groupId);
        } else {
            String sanitizedName = name.toLowerCase();
            LOGGER.info("Find boxes by group-ID {} and name {}", groupId, sanitizedName);
            return boxRepository.findByGroupIdAndName(groupId, sanitizedName);
        }*/
        return null;
    }

    @Transactional
    public Box create(final Long groupId, final ModifyBox modifyBox) {
        String sanitizedName = modifyBox.getName().toLowerCase();
        LOGGER.info("Create box with name {} for group-ID {}", sanitizedName, groupId);

        /*Optional<Group> groupOptional = groupRepository.findById(groupId);

        if (groupOptional.isPresent()) {
            Group group = groupOptional.get();
            List<Box> boxes = boxRepository.findByGroupId(group.getId());
            Box box = Box.builder()
                    .name(sanitizedName)
                    .description(modifyBox.getDescription())
                    .group(group)
                    .build();

            if (boxes.stream().anyMatch(b -> b.getName().equals(box.getName()))) {
                throw new IllegalStateException("A box with name " + box.getName() + " already exists for group " + group.getName());
            } else {
                return Optional.ofNullable(boxRepository.save(box))
                        .orElseThrow(() -> new IllegalStateException("Save returned no value"));
            }
        } else {
            throw new IllegalStateException("A group with ID " + groupId + " does not exist");
        }*/
        return null;
    }

    @SuppressWarnings("Duplicates")
    @Transactional
    public void delete(final Long boxId) {
        LOGGER.info("Delete box with ID {}", boxId);
        /*Optional<Box> boxOptional = boxRepository.findById(boxId);

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
        }*/
    }

    @Transactional
    public Box update(final Long boxId,
                      final ModifyBox modifyBox) {
        LOGGER.info("Update box with ID {}", boxId);
        /*Optional<Box> boxOptional = boxRepository.findById(boxId);

        if (boxOptional.isPresent()) {
            String sanitizedName = modifyBox.getName().toLowerCase();
            Box box = boxOptional.get();
            box.setName(sanitizedName);
            box.setDescription(modifyBox.getDescription());

            return boxRepository.save(box);
        } else {
            throw new IllegalStateException("No box found for ID " + boxId);
        }*/
        return null;
    }
}
