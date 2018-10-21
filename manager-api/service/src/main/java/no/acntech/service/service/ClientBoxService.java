package no.acntech.service.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import no.acntech.common.model.Box;
import no.acntech.common.model.ClientBox;
import no.acntech.common.model.ClientProvider;
import no.acntech.common.model.ClientVersion;
import no.acntech.common.model.Group;
import no.acntech.common.model.Provider;
import no.acntech.common.model.Version;
import no.acntech.service.repository.BoxRepository;
import no.acntech.service.repository.GroupRepository;
import no.acntech.service.repository.ProviderRepository;
import no.acntech.service.repository.VersionRepository;

@Service
public class ClientBoxService {

    private final GroupRepository groupRepository;
    private final BoxRepository boxRepository;
    private final VersionRepository versionRepository;
    private final ProviderRepository providerRepository;

    public ClientBoxService(final GroupRepository groupRepository,
                            final BoxRepository boxRepository,
                            final VersionRepository versionRepository,
                            final ProviderRepository providerRepository) {
        this.groupRepository = groupRepository;
        this.boxRepository = boxRepository;
        this.versionRepository = versionRepository;
        this.providerRepository = providerRepository;
    }

    public Optional<ClientBox> get(final String groupName, final String boxName) {
        List<Group> groups = groupRepository.findByName(groupName);

        return groups.stream()
                .filter(Objects::nonNull)
                .findFirst()
                .map(group -> mapBox(group, boxName));
    }

    private ClientBox mapBox(final Group group, final String boxName) {
        List<Box> boxes = boxRepository.findByGroupIdAndName(group.getId(), boxName.toLowerCase());

        return boxes.stream()
                .filter(Objects::nonNull)
                .findFirst()
                .map(box -> mapBox(group, box))
                .orElse(null);
    }

    private ClientBox mapBox(final Group group, final Box box) {
        List<Version> versions = versionRepository.findByBoxId(box.getId());

        List<ClientVersion> clientVersions = versions.stream()
                .map(version -> mapVersion(group, box, version))
                .collect(Collectors.toList());

        return ClientBox.builder()
                .name(determineBoxNamespace(group, box))
                .description(box.getDescription())
                .versions(clientVersions)
                .build();
    }

    private ClientVersion mapVersion(final Group group, final Box box, final Version version) {
        List<Provider> providers = providerRepository.findByVersionId(version.getId());

        List<ClientProvider> clientProviders = providers.stream()
                .filter(Objects::nonNull)
                .filter(this::filterIncompleteProvider)
                .map(provider -> mapProvider(group, box, version, provider))
                .collect(Collectors.toList());

        return ClientVersion.builder()
                .version(version.getName())
                .providers(clientProviders)
                .build();
    }

    private boolean filterIncompleteProvider(final Provider provider) {
        return provider.getSize() != null && provider.getChecksumType() != null && provider.getChecksum() != null;
    }

    private ClientProvider mapProvider(final Group group, final Box box, final Version version, final Provider provider) {
        return ClientProvider.builder()
                .name(provider.getProviderType().getName())
                .url(determineProviderUrl(group, box, version))
                .checksumType(provider.getChecksumType().getName())
                .checksum(provider.getChecksum())
                .build();
    }

    private String determineBoxNamespace(final Group group, final Box box) {
        return group.getName().toLowerCase() + "/" + box.getName().toLowerCase();
    }

    private String determineProviderUrl(final Group group, final Box box, final Version version) {
        return "http://localhost:9090/vagrant/boxes/" + determineBoxNamespace(group, box) + "/" + version.getName().toLowerCase() + "/image.box";
    }
}
