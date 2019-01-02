package no.acntech.api.resource;

import no.acntech.common.model.Provider;
import no.acntech.common.model.ProviderFile;
import no.acntech.service.service.ProviderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RequestMapping(path = "api/providers")
@RestController
public class ProviderResource {

    private final ProviderService providerService;

    public ProviderResource(final ProviderService providerService) {
        this.providerService = providerService;
    }

    @GetMapping(path = "{id}")
    public ResponseEntity<Provider> get(@PathVariable(name = "id") final Long providerId) {
        Optional<Provider> providerOptional = providerService.get(providerId);
        return providerOptional.map(ResponseEntity.ok()::body)
                .orElseGet(ResponseEntity.noContent()::build);
    }

    @PostMapping(path = "{id}")
    public ResponseEntity<Provider> post(@PathVariable(name = "id") final Long providerId,
                                         @RequestParam("file") final MultipartFile file) {
        Provider provider = providerService.update(providerId, file);
        return ResponseEntity.ok(provider);
    }

    @DeleteMapping(path = "{id}")
    public ResponseEntity delete(@PathVariable(name = "id") final Long providerId) {
        providerService.delete(providerId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(path = "{id}/files")
    public ResponseEntity<List<ProviderFile>> getFiles(@PathVariable(name = "id") final Long providerId) {
        List<ProviderFile> providerFiles = providerService.findFiles(providerId);
        return ResponseEntity.ok(providerFiles);
    }
}
