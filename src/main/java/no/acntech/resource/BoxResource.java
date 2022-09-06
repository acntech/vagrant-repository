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

import no.acntech.model.Box;
import no.acntech.model.CreateBox;
import no.acntech.model.CreateVersion;
import no.acntech.model.UpdateBox;
import no.acntech.model.UpdateVersion;
import no.acntech.model.Version;
import no.acntech.model.VersionStatus;
import no.acntech.service.BoxService;
import no.acntech.service.VersionService;

@RequestMapping(path = "/api/box")
@RestController
public class BoxResource {

    private final BoxService boxService;
    private final VersionService versionService;

    public BoxResource(final BoxService boxService,
                       final VersionService versionService) {
        this.boxService = boxService;
        this.versionService = versionService;
    }

    @GetMapping(path = "{username}/{name}")
    public ResponseEntity<Box> getBox(@PathVariable(name = "username") String username,
                                      @PathVariable(name = "name") String name) {
        final var box = boxService.getBox(username, name);
        return ResponseEntity.ok(box);
    }

    @PostMapping
    public ResponseEntity<Void> createBox(@RequestBody final CreateBox.Request createBoxRequest,
                                          final UriComponentsBuilder uriBuilder) {
        final var createBox = createBoxRequest.box();
        boxService.createBox(createBox);
        final var uri = uriBuilder.path("/api/box/{username}/{name}")
                .buildAndExpand(createBox.username().toLowerCase(), createBox.name().toLowerCase())
                .toUri();
        return ResponseEntity.created(uri).build();
    }

    @PutMapping(path = "{username}/{name}")
    public ResponseEntity<Void> updateBox(@PathVariable(name = "username") String username,
                                          @PathVariable(name = "name") String name,
                                          @RequestBody final UpdateBox.Request updateBoxRequest) {
        final var updateBox = updateBoxRequest.box();
        boxService.updateBox(username, name, updateBox);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(path = "{username}/{name}")
    public ResponseEntity<Void> deleteBox(@PathVariable(name = "username") String username,
                                          @PathVariable(name = "name") String name) {
        boxService.deleteBox(username, name);
        return ResponseEntity.ok().build();
    }

    @GetMapping(path = "{username}/{name}/version/{version}")
    public ResponseEntity<Version> getVersion(@PathVariable(name = "username") String username,
                                              @PathVariable(name = "name") String name,
                                              @PathVariable(name = "version") String versionParam) {
        final var version = versionService.getVersion(username, name, versionParam);
        return ResponseEntity.ok(version);
    }

    @PostMapping(path = "{username}/{name}/version")
    public ResponseEntity<Version> createVersion(@PathVariable(name = "username") String username,
                                                 @PathVariable(name = "name") String name,
                                                 @RequestBody final CreateVersion.Request createVersionRequest,
                                                 final UriComponentsBuilder uriBuilder) {
        final var createVersion = createVersionRequest.version();
        versionService.createVersion(username, name, createVersion);
        final var uri = uriBuilder.path("/api/box/{username}/{name}/version/{version}")
                .buildAndExpand(username.toLowerCase(), name.toLowerCase(), createVersion.version())
                .toUri();
        return ResponseEntity.created(uri).build();
    }

    @PutMapping(path = "{username}/{name}/version/{version}")
    public ResponseEntity<Version> updateVersion(@PathVariable(name = "username") String username,
                                                 @PathVariable(name = "name") String name,
                                                 @PathVariable(name = "version") String version,
                                                 @RequestBody final UpdateVersion.Request updateVersionRequest) {
        final var updateVersion = updateVersionRequest.version();
        versionService.updateVersion(username, name, version, updateVersion);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(path = "{username}/{name}/version/{version}")
    public ResponseEntity<Version> deleteVersion(@PathVariable(name = "username") String username,
                                                 @PathVariable(name = "name") String name,
                                                 @PathVariable(name = "version") String version) {
        versionService.deleteVersion(username, name, version);
        return ResponseEntity.ok().build();
    }

    @PutMapping(path = "{username}/{name}/version/{version}/release")
    public ResponseEntity<Version> releaseVersion(@PathVariable(name = "username") String username,
                                                  @PathVariable(name = "name") String name,
                                                  @PathVariable(name = "version") String version) {
        versionService.updateVersionStatus(username, name, version, VersionStatus.ACTIVE);
        return ResponseEntity.ok().build();
    }

    @PutMapping(path = "{username}/{name}/version/{version}/revoke")
    public ResponseEntity<Version> revokeVersion(@PathVariable(name = "username") String username,
                                                 @PathVariable(name = "name") String name,
                                                 @PathVariable(name = "version") String version) {
        versionService.updateVersionStatus(username, name, version, VersionStatus.INACTIVE);
        return ResponseEntity.ok().build();
    }
}
