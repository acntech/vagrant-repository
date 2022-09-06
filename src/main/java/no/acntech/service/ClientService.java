package no.acntech.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

import no.acntech.exception.ItemNotFoundException;
import no.acntech.model.ClientBox;
import no.acntech.model.ProviderType;
import no.acntech.properties.ApplicationProperties;

@Service
public class ClientService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientService.class);

    private final ApplicationProperties applicationProperties;
    private final FileService fileService;

    public ClientService(final ApplicationProperties applicationProperties,
                         final FileService fileService) {
        this.applicationProperties = applicationProperties;
        this.fileService = fileService;
    }

    public Optional<ClientBox> get(String groupName,
                                   String boxName,
                                   final UriComponentsBuilder uriBuilder) {
        LOGGER.info("Getting client information about {}/{}", groupName, boxName);
        return null;
    }

    public Resource getFile(String groupName,
                            String boxName,
                            String versionName,
                            String providerName,
                            String fileName) {
        LOGGER.info("Getting client file information about {}/{}/{}/{}", groupName, boxName, versionName, providerName);
        ProviderType providerType = ProviderType.fromName(providerName);

        if (providerType == null) {
            throw new ItemNotFoundException("Unknown provider");
        }

        return fileService.loadFile(groupName, boxName, versionName, providerType, fileName);
    }
}
