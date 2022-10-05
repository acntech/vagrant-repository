package no.acntech.resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import no.acntech.model.Box;
import no.acntech.model.CreateBox;
import no.acntech.model.CreateProvider;
import no.acntech.model.CreateVersion;
import no.acntech.model.Provider;
import no.acntech.model.ProviderType;
import no.acntech.model.UpdateBox;
import no.acntech.model.UpdateProvider;
import no.acntech.model.UpdateVersion;
import no.acntech.model.Upload;
import no.acntech.model.Version;
import no.acntech.model.VersionStatus;
import no.acntech.service.BoxService;
import no.acntech.service.DecoratorService;
import no.acntech.service.ProviderService;
import no.acntech.service.UploadService;
import no.acntech.service.VersionService;
import no.acntech.util.UrlBuilder;

@RequestMapping(path = "/api/v1/box")
@RestController
public class BoxResource {

    private final BoxService boxService;
    private final VersionService versionService;
    private final ProviderService providerService;
    private final UploadService uploadService;
    private final DecoratorService decoratorService;

    public BoxResource(final BoxService boxService,
                       final VersionService versionService,
                       final ProviderService providerService,
                       final UploadService uploadService,
                       final DecoratorService decoratorService) {
        this.boxService = boxService;
        this.versionService = versionService;
        this.providerService = providerService;
        this.uploadService = uploadService;
        this.decoratorService = decoratorService;
    }

    @GetMapping(path = "{username}/{name}")
    public ResponseEntity<Box> getBox(@PathVariable(name = "username") final String username,
                                      @PathVariable(name = "name") final String name,
                                      final UriComponentsBuilder uriBuilder) {
        final var box = boxService.getBox(username, name);
        final var decoratedBox = decoratorService.decorateBox(box, uriBuilder);
        return ResponseEntity.ok(decoratedBox);
    }

    @PostMapping
    public ResponseEntity<Void> createBox(@RequestBody @Valid @NotNull final CreateBox.Request createBoxRequest,
                                          final UriComponentsBuilder uriBuilder) {
        final var createBox = createBoxRequest.box();
        boxService.createBox(createBox);
        final var uri = UrlBuilder.boxUri(uriBuilder, createBox.username(), createBox.name());
        return ResponseEntity.created(uri).build();
    }

    @PutMapping(path = "{username}/{name}")
    public ResponseEntity<Void> updateBox(@PathVariable(name = "username") final String username,
                                          @PathVariable(name = "name") final String name,
                                          @RequestBody @Valid @NotNull final UpdateBox.Request updateBoxRequest) {
        final var updateBox = updateBoxRequest.box();
        boxService.updateBox(username, name, updateBox);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(path = "{username}/{name}")
    public ResponseEntity<Void> deleteBox(@PathVariable(name = "username") final String username,
                                          @PathVariable(name = "name") final String name) {
        boxService.deleteBox(username, name);
        return ResponseEntity.ok().build();
    }

    @GetMapping(path = "{username}/{name}/version/{version}")
    public ResponseEntity<Version> getVersion(@PathVariable(name = "username") final String username,
                                              @PathVariable(name = "name") final String name,
                                              @PathVariable(name = "version") final String versionParam,
                                              final UriComponentsBuilder uriBuilder) {
        final var version = versionService.getVersion(username, name, versionParam);
        final var providers = providerService.findProviders(username, name, versionParam);
        final var releaseUri = UrlBuilder.versionReleaseUri(uriBuilder, username.toLowerCase(), name.toLowerCase(), version.version());
        final var revokeUri = UrlBuilder.versionRevokeUri(uriBuilder, username.toLowerCase(), name.toLowerCase(), version.version());
        return ResponseEntity.ok(version.with(releaseUri.toString(), revokeUri.toString(), providers));
    }

    @PostMapping(path = "{username}/{name}/version")
    public ResponseEntity<Version> createVersion(@PathVariable(name = "username") final String username,
                                                 @PathVariable(name = "name") final String name,
                                                 @RequestBody @Valid @NotNull final CreateVersion.Request createVersionRequest,
                                                 final UriComponentsBuilder uriBuilder) {
        final var createVersion = createVersionRequest.version();
        versionService.createVersion(username, name, createVersion);
        final var uri = UrlBuilder.versionUri(uriBuilder, username, name, createVersion.version());
        return ResponseEntity.created(uri).build();
    }

    @PutMapping(path = "{username}/{name}/version/{version}")
    public ResponseEntity<Version> updateVersion(@PathVariable(name = "username") final String username,
                                                 @PathVariable(name = "name") final String name,
                                                 @PathVariable(name = "version") final String version,
                                                 @RequestBody @Valid @NotNull final UpdateVersion.Request updateVersionRequest) {
        final var updateVersion = updateVersionRequest.version();
        versionService.updateVersion(username, name, version, updateVersion);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(path = "{username}/{name}/version/{version}")
    public ResponseEntity<Version> deleteVersion(@PathVariable(name = "username") final String username,
                                                 @PathVariable(name = "name") final String name,
                                                 @PathVariable(name = "version") final String version) {
        versionService.deleteVersion(username, name, version);
        return ResponseEntity.ok().build();
    }

    @PutMapping(path = "{username}/{name}/version/{version}/release")
    public ResponseEntity<Version> releaseVersion(@PathVariable(name = "username") final String username,
                                                  @PathVariable(name = "name") final String name,
                                                  @PathVariable(name = "version") final String version) {
        versionService.updateVersionStatus(username, name, version, VersionStatus.ACTIVE);
        return ResponseEntity.ok().build();
    }

    @PutMapping(path = "{username}/{name}/version/{version}/revoke")
    public ResponseEntity<Version> revokeVersion(@PathVariable(name = "username") final String username,
                                                 @PathVariable(name = "name") final String name,
                                                 @PathVariable(name = "version") final String version) {
        versionService.updateVersionStatus(username, name, version, VersionStatus.UNRELEASED);
        return ResponseEntity.ok().build();
    }

    @GetMapping(path = "{username}/{name}/version/{version}/provider/{provider}")
    public ResponseEntity<Provider> getProvider(@PathVariable(name = "username") final String username,
                                                @PathVariable(name = "name") final String name,
                                                @PathVariable(name = "version") final String version,
                                                @PathVariable(name = "provider") final String providerParam,
                                                final UriComponentsBuilder uriBuilder) {
        final var providerType = ProviderType.fromProvider(providerParam);
        final var provider = providerService.getProvider(username, name, version, providerType);
        if (provider.hosted()) {
            final var upload = uploadService.getUpload(username, name, version, providerType);
            final var uploadPathUri = UrlBuilder.uploadPathUri(uriBuilder, upload.uid());
            return ResponseEntity.ok(provider.with(uploadPathUri.toString()));
        } else {
            return ResponseEntity.ok(provider.with(provider.originalUrl()));
        }
    }

    @PostMapping(path = "{username}/{name}/version/{version}/provider")
    public ResponseEntity<Version> createProvider(@PathVariable(name = "username") final String username,
                                                  @PathVariable(name = "name") final String name,
                                                  @PathVariable(name = "version") final String version,
                                                  @RequestBody @Valid @NotNull final CreateProvider.Request createProviderRequest,
                                                  final UriComponentsBuilder uriBuilder) {
        final var createProvider = createProviderRequest.provider();
        providerService.createProvider(username, name, version, createProvider);
        final var uri = UrlBuilder.providerUri(uriBuilder, username, name, version, createProvider.name());
        return ResponseEntity.created(uri).build();
    }

    @PutMapping(path = "{username}/{name}/version/{version}/provider/{provider}")
    public ResponseEntity<Version> updateProvider(@PathVariable(name = "username") final String username,
                                                  @PathVariable(name = "name") final String name,
                                                  @PathVariable(name = "version") final String version,
                                                  @PathVariable(name = "provider") final String provider,
                                                  @RequestBody @Valid @NotNull final UpdateProvider.Request updateProviderRequest) {
        final var updateProvider = updateProviderRequest.provider();
        providerService.updateProvider(username, name, version, ProviderType.fromProvider(provider), updateProvider);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(path = "{username}/{name}/version/{version}/provider/{provider}")
    public ResponseEntity<Version> deleteProvider(@PathVariable(name = "username") final String username,
                                                  @PathVariable(name = "name") final String name,
                                                  @PathVariable(name = "version") final String version,
                                                  @PathVariable(name = "provider") final String provider) {
        providerService.deleteProvider(username, name, version, ProviderType.fromProvider(provider));
        return ResponseEntity.ok().build();
    }

    @GetMapping(path = "{username}/{name}/version/{version}/provider/{provider}/upload")
    public ResponseEntity<Upload> getUpload(@PathVariable(name = "username") final String username,
                                            @PathVariable(name = "name") final String name,
                                            @PathVariable(name = "version") final String version,
                                            @PathVariable(name = "provider") final String providerParam,
                                            final UriComponentsBuilder uriBuilder) {
        final var uid = uploadService.createUpload(username, name, version, ProviderType.fromProvider(providerParam));
        final var upload = uploadService.getUpload(uid);
        final var uri = UrlBuilder.uploadPathUri(uriBuilder, uid);
        return ResponseEntity.ok(upload.with(uri.toString()));
    }
}
