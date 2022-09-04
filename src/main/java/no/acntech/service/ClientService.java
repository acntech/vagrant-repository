package no.acntech.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

import no.acntech.exception.UnknownProviderException;
import no.acntech.model.Box;
import no.acntech.model.ClientBox;
import no.acntech.model.ClientProvider;
import no.acntech.model.ClientVersion;
import no.acntech.model.Group;
import no.acntech.model.Provider;
import no.acntech.model.ProviderType;
import no.acntech.model.Version;
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
        /*List<Group> groups = groupRepository.findByName(groupName);

        return groups.stream()
                .filter(Objects::nonNull)
                .findFirst()
                .map(group -> mapBox(group, boxName, uriBuilder));*/
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
            throw new UnknownProviderException("Unknown provider");
        }

        return fileService.loadFile(groupName, boxName, versionName, providerType, fileName);
    }

    private ClientBox mapBox(final Group group,
                             String boxName,
                             final UriComponentsBuilder uriBuilder) {
        /*List<Box> boxes = boxRepository.findByGroupIdAndName(group.getId(), boxName.toLowerCase());

        return boxes.stream()
                .filter(Objects::nonNull)
                .findFirst()
                .map(box -> mapBox(group, box, uriBuilder))
                .orElse(null);*/
        return null;
    }

    private ClientBox mapBox(final Group group,
                             final Box box,
                             final UriComponentsBuilder uriBuilder) {
        /*List<Version> versions = versionRepository.findByBoxId(box.getId());

        List<ClientVersion> clientVersions = versions.stream()
                .map(version -> mapVersion(group, box, version, uriBuilder))
                .collect(Collectors.toList());*/

        return ClientBox.builder()
                .name(determineBoxNamespace(group, box))
                .description(box.getDescription())
                .versions(null)
                .build();
    }

    private ClientVersion mapVersion(final Group group,
                                     final Box box,
                                     final Version version,
                                     final UriComponentsBuilder uriBuilder) {
        /*List<Provider> providers = providerRepository.findByVersionId(version.getId());

        List<ClientProvider> clientProviders = providers.stream()
                .filter(Objects::nonNull)
                .filter(this::filterIncompleteProvider)
                .map(provider -> mapProvider(group, box, version, provider, uriBuilder))
                .collect(Collectors.toList());*/

        return ClientVersion.builder()
                .version(version.getName())
                //.providers(clientProviders)
                .build();
    }

    private boolean filterIncompleteProvider(final Provider provider) {
        return provider.getSize() != null && provider.getChecksumType() != null && provider.getChecksum() != null;
    }

    private ClientProvider mapProvider(final Group group,
                                       final Box box,
                                       final Version version,
                                       final Provider provider,
                                       final UriComponentsBuilder uriBuilder) {
        return ClientProvider.builder()
                .name(provider.getProviderType().getName())
                .url(determineProviderUrl(group, box, version, provider, uriBuilder))
                .checksumType(provider.getChecksumType().getName())
                .checksum(provider.getChecksum())
                .build();
    }

    private String determineBoxNamespace(final Group group,
                                         final Box box) {
        String groupName = group.getName().toLowerCase();
        String boxName = box.getName().toLowerCase();
        return groupName + "/" + boxName;
    }

    private String determineProviderUrl(final Group group,
                                        final Box box,
                                        final Version version,
                                        final Provider provider,
                                        final UriComponentsBuilder uriBuilder) {
        String contextPath = applicationProperties.getApi().getClientContextPath();
        String fileName = applicationProperties.getFile().getDefaultFileName();
        String groupName = group.getName().toLowerCase();
        String boxName = box.getName().toLowerCase();
        String versionName = version.getName().toLowerCase();
        String providerName = provider.getProviderType().getName();

        if (applicationProperties.getProxy().isSet()) {
            String scheme = applicationProperties.getProxy().getScheme();
            String host = applicationProperties.getProxy().getHost();
            Integer port = applicationProperties.getProxy().getPort();
            if (port == 80) {
                return UriComponentsBuilder.newInstance()
                        .scheme(scheme)
                        .host(host)
                        .path("{contextPath}/{groupName}/{boxName}/{versionName}/{providerName}/{fileName}")
                        .buildAndExpand(contextPath, groupName, boxName, versionName, providerName, fileName)
                        .toUri()
                        .toString();
            } else {
                return UriComponentsBuilder.newInstance()
                        .scheme(scheme)
                        .host(host)
                        .port(port)
                        .path("{contextPath}/{groupName}/{boxName}/{versionName}/{providerName}/{fileName}")
                        .buildAndExpand(contextPath, groupName, boxName, versionName, providerName, fileName)
                        .toUri()
                        .toString();
            }
        } else {
            return uriBuilder
                    .path("{contextPath}/{groupName}/{boxName}/{versionName}/{providerName}/{fileName}")
                    .buildAndExpand(contextPath, groupName, boxName, versionName, providerName, fileName)
                    .toUri()
                    .toString();
        }
    }
}
