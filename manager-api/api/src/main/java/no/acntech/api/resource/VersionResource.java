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

import no.acntech.common.model.Provider;
import no.acntech.common.model.ProviderType;
import no.acntech.common.model.Version;
import no.acntech.service.service.ProviderService;
import no.acntech.service.service.VersionService;

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

    @GetMapping(path = "{id}/providers")
    public ResponseEntity<List<Provider>> findVersionProviders(@PathVariable(name = "id") final Long versionId,
                                                               @RequestParam(name = "type", required = false) final ProviderType providerType) {
        List<Provider> providers = providerService.find(versionId, providerType);
        return ResponseEntity.ok(providers);
    }

    @PostMapping(path = "{id}/providers")
    public ResponseEntity<Provider> post(@PathVariable(name = "id") final Long versionId,
                                         @RequestBody final Provider provider, UriComponentsBuilder uriBuilder) {
        Provider createdProvider = providerService.create(versionId, provider);
        URI uri = uriBuilder.path("providers/{id}").buildAndExpand(createdProvider.getId()).toUri();
        return ResponseEntity.created(uri).body(createdProvider);
    }
}
