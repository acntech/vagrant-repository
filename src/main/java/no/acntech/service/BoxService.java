package no.acntech.service;

import org.apache.commons.lang3.tuple.Pair;
import org.jooq.exception.NoDataFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

import no.acntech.annotation.Permission;
import no.acntech.exception.ItemNotFoundException;
import no.acntech.exception.SaveItemFailedException;
import no.acntech.model.Action;
import no.acntech.model.Box;
import no.acntech.model.CreateBox;
import no.acntech.model.Resource;
import no.acntech.model.UpdateBox;
import no.acntech.repository.BoxRepository;

@Validated
@Service
public class BoxService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BoxService.class);
    private final ConversionService conversionService;
    private final OrganizationService organizationService;
    private final BoxRepository boxRepository;

    public BoxService(final ConversionService conversionService,
                      final OrganizationService organizationService,
                      final BoxRepository boxRepository) {
        this.conversionService = conversionService;
        this.organizationService = organizationService;
        this.boxRepository = boxRepository;
    }

    @Permission(action = Action.READ, resource = Resource.BOXES)
    public Box getBox(@NotBlank final String username,
                      @NotBlank final String name) {
        final var tag = username + "/" + name;
        LOGGER.info("Get box {}", tag);
        final var organization = organizationService.getOrganization(username);
        try {
            final var record = boxRepository.getBox(name, organization.id());
            final var pair = Pair.of(record, organization);
            return conversionService.convert(pair, Box.class);
        } catch (NoDataFoundException e) {
            throw new ItemNotFoundException("No box found for tag " + tag, e);
        }
    }

    @Permission(action = Action.READ, resource = Resource.BOXES)
    public List<Box> findBoxes(@NotBlank final String username) {
        LOGGER.info("Find boxes for {}", username);
        final var organization = organizationService.getOrganization(username);
        final var result = boxRepository.findBoxes(organization.id());
        return result.stream()
                .map(record -> Pair.of(record, organization))
                .map(pair -> conversionService.convert(pair, Box.class))
                .collect(Collectors.toList());
    }

    @Permission(action = Action.CREATE, resource = Resource.BOXES)
    @Transactional
    public void createBox(@Valid @NotNull final CreateBox createBox) {
        final var tag = createBox.username() + "/" + createBox.name();
        LOGGER.info("Create box {}", tag);
        final var organization = organizationService.getOrganization(createBox.username());
        final var rowsAffected = boxRepository.createBox(
                createBox.name().toLowerCase(),
                createBox.description(),
                createBox.isPrivate(),
                organization.id());
        LOGGER.debug("Insert into BOXES table affected {} rows", rowsAffected);
        if (rowsAffected == 0) {
            throw new SaveItemFailedException("Failed to create box " + tag);
        }
    }

    @Permission(action = Action.UPDATE, resource = Resource.BOXES)
    @Transactional
    public void updateBox(@NotBlank final String username,
                          @NotBlank final String name,
                          @Valid @NotNull final UpdateBox updateBox) {
        final var tag = username + "/" + name;
        LOGGER.info("Update box {}", tag);
        final var organization = organizationService.getOrganization(username);
        final var record = boxRepository.getBox(username, organization.id());
        final var rowsAffected = boxRepository.updateBox(
                updateBox.name().toLowerCase(),
                updateBox.description(),
                updateBox.isPrivate(),
                record.getId());
        LOGGER.debug("Updated record in BOXES table affected {} rows", rowsAffected);
        if (rowsAffected == 0) {
            throw new SaveItemFailedException("Failed to update box " + tag);
        }
    }

    @Permission(action = Action.DELETE, resource = Resource.BOXES)
    @Transactional
    public void deleteBox(@NotBlank final String username,
                          @NotBlank final String name) {
        final var tag = username + "/" + name;
        LOGGER.info("Delete box {}", tag);
        final var organization = organizationService.getOrganization(username);
        final var record = boxRepository.getBox(username, organization.id());
        final var rowsAffected = boxRepository.deleteBox(record.getId());
        LOGGER.debug("Delete record in BOXES table affected {} rows", rowsAffected);
        if (rowsAffected == 0) {
            throw new SaveItemFailedException("Failed to delete box " + tag);
        }
    }

    @Transactional
    public void postDownload(@NotNull final Integer boxId) {
        final var record = boxRepository.getBox(boxId);
        final var downloads = record.getDownloads() + 1;
        final var rowsAffected = boxRepository.updateBoxDownload(downloads, record.getId());
        LOGGER.debug("Updated record in BOXES table affected {} rows", rowsAffected);
        if (rowsAffected == 0) {
            throw new SaveItemFailedException("Failed to update box with ID " + boxId);
        }
    }
}
