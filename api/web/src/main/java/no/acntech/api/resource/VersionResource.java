package no.acntech.api.resource;

import no.acntech.common.model.*;
import no.acntech.service.service.ProviderService;
import no.acntech.service.service.VersionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@RequestMapping(path = "versions")
@RestController
public class VersionResource {

    private final VersionService versionService;
    private final ProviderService providerService;

    public VersionResource(final VersionService versionService,
                           final ProviderService providerService) {
        this.versionService = versionService;
        this.providerService = providerService;
    }

    @GetMapping(path = "{id}")
    public ResponseEntity<Version> get(@PathVariable(name = "id") final Long versionId) {
        Optional<Version> versionOptional = versionService.get(versionId);
        return versionOptional.map(ResponseEntity.ok()::body)
                .orElseGet(ResponseEntity.noContent()::build);
    }

    @DeleteMapping(path = "{id}")
    public ResponseEntity delete(@PathVariable(name = "id") final Long versionId) {
        versionService.delete(versionId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(path = "{id}")
    public ResponseEntity<Version> put(@PathVariable(name = "id") final Long versionId,
                                       @Valid @RequestBody final ModifyVersion modifyVersion) {
        Version version = versionService.update(versionId, modifyVersion);
        return ResponseEntity.ok(version);
    }

    @GetMapping(path = "{id}/providers")
    public ResponseEntity<List<Provider>> findVersionProviders(@PathVariable(name = "id") final Long versionId,
                                                               @RequestParam(name = "type", required = false) final ProviderType providerType) {
        List<Provider> providers = providerService.find(versionId, providerType);
        return ResponseEntity.ok(providers);
    }

    @PostMapping(path = "{id}/providers")
    public ResponseEntity<Provider> post(@PathVariable(name = "id") final Long versionId,
                                         @Valid @RequestBody final ModifyProvider modifyProvider,
                                         final UriComponentsBuilder uriBuilder) {
        Provider createdProvider = providerService.create(versionId, modifyProvider);
        URI uri = uriBuilder.path("providers/{id}").buildAndExpand(createdProvider.getId()).toUri();
        return ResponseEntity.created(uri).body(createdProvider);
    }
}
