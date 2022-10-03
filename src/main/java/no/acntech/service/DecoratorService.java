package no.acntech.service;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Comparator;

import no.acntech.model.Box;
import no.acntech.model.Provider;
import no.acntech.model.Version;
import no.acntech.util.UrlBuilder;

@Validated
@Service
public class DecoratorService {

    private final VersionService versionService;
    private final ProviderService providerService;
    private final UploadService uploadService;

    public DecoratorService(final VersionService versionService,
                            final ProviderService providerService,
                            final UploadService uploadService) {
        this.versionService = versionService;
        this.providerService = providerService;
        this.uploadService = uploadService;
    }

    public Box decorateBox(final Box box,
                           final UriComponentsBuilder uriBuilder) {
        final var versions = versionService.findVersions(box.username(), box.name());
        final var decoratedVersions = versions.stream()
                .map(version -> decorateVersion(box, version, uriBuilder))
                .sorted(Comparator.comparingInt(Version::id).reversed())
                .toList();
        final var currentVersion = decoratedVersions.isEmpty() ? null : decoratedVersions.get(0);
        return box.with(currentVersion, decoratedVersions);
    }

    public Version decorateVersion(final Box box,
                                   final Version version,
                                   final UriComponentsBuilder uriBuilder) {
        final var releaseUri = UrlBuilder.versionReleaseUri(uriBuilder, box.username(), box.name(), version.name());
        final var revokeUri = UrlBuilder.versionRevokeUri(uriBuilder, box.username(), box.name(), version.name());
        final var providers = providerService.findProviders(box.username(), box.name(), version.name());
        final var decorateProviders = providers.stream()
                .map(provider -> decorateProvider(box, version, provider, uriBuilder))
                .toList();
        return version.with(releaseUri.toString(), revokeUri.toString(), decorateProviders);
    }

    private Provider decorateProvider(final Box box,
                                      final Version version,
                                      final Provider provider,
                                      final UriComponentsBuilder uriBuilder) {
        if (provider.hosted()) {
            final var upload = uploadService.getUpload(box.username(), box.name(), version.name(), provider.name());
            final var uploadPathUri = UrlBuilder.uploadPathUri(uriBuilder, upload.uid());
            return provider.with(uploadPathUri.toString());
        } else {
            return provider.with(provider.originalUrl());
        }
    }
}
