package no.acntech.resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import no.acntech.model.Provider;
import no.acntech.model.ProviderFile;
import no.acntech.service.ProviderService;

@RequestMapping(path = "providers")
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
    public ResponseEntity<Void> delete(@PathVariable(name = "id") final Long providerId) {
        providerService.delete(providerId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(path = "{id}/files")
    public ResponseEntity<List<ProviderFile>> getFiles(@PathVariable(name = "id") final Long providerId) {
        List<ProviderFile> providerFiles = providerService.findFiles(providerId);
        return ResponseEntity.ok(providerFiles);
    }
}
