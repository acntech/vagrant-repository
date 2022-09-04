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

@Service
public class GroupService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GroupService.class);
    private final FileService fileService;

    public GroupService(final FileService fileService) {
        this.fileService = fileService;
    }

    public Optional<Group> get(final Long groupId) {
        LOGGER.info("Get group with ID {}", groupId);
        return null;
    }

    public List<Group> find(final String name) {
        if (name == null) {
            LOGGER.info("Find groups");
            return null;
        } else {
            String sanitizedName = name.toLowerCase();
            LOGGER.info("Find groups by name {}", sanitizedName);
            return null;
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
        return null;
    }

    @SuppressWarnings("Duplicates")
    @Transactional
    public void delete(final Long groupId) {
        LOGGER.info("Delete group with ID {}", groupId);
    }

    @Transactional
    public Group update(final Long groupId,
                        @Valid final ModifyGroup modifyGroup) {
        LOGGER.info("Update group with ID {}", groupId);
        return null;
    }
}
