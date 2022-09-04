package no.acntech.resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import no.acntech.model.Box;
import no.acntech.model.ModifyBox;
import no.acntech.model.ModifyVersion;
import no.acntech.model.Version;
import no.acntech.service.BoxService;
import no.acntech.service.VersionService;

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

    @DeleteMapping(path = "{id}")
    public ResponseEntity<Void> delete(@PathVariable(name = "id") final Long boxId) {
        boxService.delete(boxId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(path = "{id}")
    public ResponseEntity<Box> put(@PathVariable(name = "id") final Long boxId,
                                   @Valid @RequestBody final ModifyBox modifyBox) {
        Box box = boxService.update(boxId, modifyBox);
        return ResponseEntity.ok(box);
    }

    @GetMapping(path = "{id}/versions")
    public ResponseEntity<List<Version>> findBoxVersions(@PathVariable(name = "id") final Long boxId,
                                                         @RequestParam(name = "name", required = false) final String name) {
        List<Version> versions = versionService.find(boxId, name);
        return ResponseEntity.ok(versions);
    }

    @PostMapping(path = "{id}/versions")
    public ResponseEntity<Version> post(@PathVariable(name = "id") final Long boxId,
                                        @Valid @RequestBody final ModifyVersion modifyVersion,
                                        final UriComponentsBuilder uriBuilder) {
        Version createdVersion = versionService.create(boxId, modifyVersion);
        URI uri = uriBuilder.path("versions/{id}").buildAndExpand(createdVersion.getId()).toUri();
        return ResponseEntity.created(uri).body(createdVersion);
    }
}
