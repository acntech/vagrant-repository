package no.acntech.service.service;

import no.acntech.common.config.ApplicationProperties;
import no.acntech.common.model.*;
import no.acntech.service.repository.BoxRepository;
import no.acntech.service.repository.GroupRepository;
import no.acntech.service.repository.ProviderRepository;
import no.acntech.service.repository.VersionRepository;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClientBoxService {

    private final ApplicationProperties applicationProperties;
    private final GroupRepository groupRepository;
    private final BoxRepository boxRepository;
    private final VersionRepository versionRepository;
    private final ProviderRepository providerRepository;
    private final FileService fileService;

    public ClientBoxService(final ApplicationProperties applicationProperties,
                            final GroupRepository groupRepository,
                            final BoxRepository boxRepository,
                            final VersionRepository versionRepository,
                            final ProviderRepository providerRepository,
                            final FileService fileService) {
        this.applicationProperties = applicationProperties;
        this.groupRepository = groupRepository;
        this.boxRepository = boxRepository;
        this.versionRepository = versionRepository;
        this.providerRepository = providerRepository;
        this.fileService = fileService;
    }

    public Optional<ClientBox> get(final String groupName, final String boxName, UriComponentsBuilder uriBuilder) {
        List<Group> groups = groupRepository.findByName(groupName);

        return groups.stream()
                .filter(Objects::nonNull)
                .findFirst()
                .map(group -> mapBox(group, boxName, uriBuilder));
    }

    public Resource getFile(String groupName,
                            String boxName,
                            String versionName,
                            String providerName,
                            String fileName) {
        ProviderType providerType = ProviderType.valueOf(providerName);
        return fileService.loadFile(groupName, boxName, versionName, providerType, fileName);
    }

    private ClientBox mapBox(final Group group, final String boxName, UriComponentsBuilder uriBuilder) {
        List<Box> boxes = boxRepository.findByGroupIdAndName(group.getId(), boxName.toLowerCase());

        return boxes.stream()
                .filter(Objects::nonNull)
                .findFirst()
                .map(box -> mapBox(group, box, uriBuilder))
                .orElse(null);
    }

    private ClientBox mapBox(final Group group, final Box box, UriComponentsBuilder uriBuilder) {
        List<Version> versions = versionRepository.findByBoxId(box.getId());

        List<ClientVersion> clientVersions = versions.stream()
                .map(version -> mapVersion(group, box, version, uriBuilder))
                .collect(Collectors.toList());

        return ClientBox.builder()
                .name(determineBoxNamespace(group, box))
                .description(box.getDescription())
                .versions(clientVersions)
                .build();
    }

    private ClientVersion mapVersion(final Group group, final Box box, final Version version, UriComponentsBuilder uriBuilder) {
        List<Provider> providers = providerRepository.findByVersionId(version.getId());

        List<ClientProvider> clientProviders = providers.stream()
                .filter(Objects::nonNull)
                .filter(this::filterIncompleteProvider)
                .map(provider -> mapProvider(group, box, version, provider, uriBuilder))
                .collect(Collectors.toList());

        return ClientVersion.builder()
                .version(version.getName())
                .providers(clientProviders)
                .build();
    }

    private boolean filterIncompleteProvider(final Provider provider) {
        return provider.getSize() != null && provider.getChecksumType() != null && provider.getChecksum() != null;
    }

    private ClientProvider mapProvider(final Group group, final Box box, final Version version, final Provider provider, UriComponentsBuilder uriBuilder) {
        return ClientProvider.builder()
                .name(provider.getProviderType().getName())
                .url(determineProviderUrl(group, box, version, provider, uriBuilder))
                .checksumType(provider.getChecksumType().getName())
                .checksum(provider.getChecksum())
                .build();
    }

    private String determineBoxNamespace(final Group group, final Box box) {
        String groupName = group.getName().toLowerCase();
        String boxName = box.getName().toLowerCase();
        return groupName + "/" + boxName;
    }

    private String determineProviderUrl(final Group group, final Box box, final Version version, final Provider provider, final UriComponentsBuilder uriBuilder) {
        String contextPath = applicationProperties.getFile().getRootContextPath();
        String fileName = applicationProperties.getFile().getDefaultFileName();
        String groupName = group.getName().toLowerCase();
        String boxName = box.getName().toLowerCase();
        String versionName = version.getName().toLowerCase();
        String providerName = provider.getProviderType().getName();
        URI uri = uriBuilder
                .path("{contextPath}/{groupName}/{boxName}/{versionName}/{providerName}/{fileName}")
                .buildAndExpand(contextPath, groupName, boxName, versionName, providerName, fileName)
                .toUri();
        return uri.toString();
    }
}
