package no.acntech.api.resource;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import no.acntech.common.model.Box;
import no.acntech.common.model.Version;
import no.acntech.service.service.BoxService;
import no.acntech.service.service.VersionService;

@RequestMapping(path = "boxes")
@RestController
public class BoxResource {

    private final BoxService boxService;
    private final VersionService versionService;

    public BoxResource(final BoxService boxService,
                       final VersionService versionService) {
        this.boxService = boxService;
        this.versionService = versionService;
    }

    @GetMapping(path = "{id}")
    public ResponseEntity<Box> get(@PathVariable(name = "id") final Long boxId) {
        Optional<Box> boxOptional = boxService.get(boxId);
        return boxOptional.map(ResponseEntity.ok()::body)
                .orElseGet(ResponseEntity.noContent()::build);
    }

    @GetMapping(path = "{id}/versions")
    public ResponseEntity<List<Version>> findBoxVersions(@PathVariable(name = "id") final Long boxId,
                                                         @RequestParam(name = "name", required = false) final String name) {
        List<Version> versions = versionService.find(boxId, name);
        return ResponseEntity.ok(versions);
    }

    @PostMapping(path = "{id}/versions")
    public ResponseEntity<Version> post(@PathVariable(name = "id") final Long boxId,
                                        @RequestBody final Version version,
                                        UriComponentsBuilder uriBuilder) {
        Version createdVersion = versionService.create(boxId, version);
        URI uri = uriBuilder.path("versions/{id}").buildAndExpand(createdVersion.getId()).toUri();
        return ResponseEntity.created(uri).body(createdVersion);
    }
}
