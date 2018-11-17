package no.acntech.api.resource;

import java.util.Optional;

import no.acntech.service.service.FileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import no.acntech.common.model.Provider;
import no.acntech.service.service.ProviderService;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping(path = "api/providers")
@RestController
public class ProviderResource {

    private final ProviderService providerService;
    private final FileService fileService;

    public ProviderResource(final ProviderService providerService,
                            final FileService fileService) {
        this.providerService = providerService;
        this.fileService = fileService;
    }

    @GetMapping(path = "{id}")
    public ResponseEntity<Provider> get(@PathVariable(name = "id") final Long providerId) {
        Optional<Provider> providerOptional = providerService.get(providerId);
        return providerOptional.map(ResponseEntity.ok()::body)
                .orElseGet(ResponseEntity.noContent()::build);
    }

    @PostMapping(path = "{id}")
    public ResponseEntity<Provider> postFile(@PathVariable(name = "id") final Long providerId,
                         @RequestParam("file") final MultipartFile file) {
        Optional<Provider> provider = fileService.uploadFile(providerId, file);
        return provider.map(ResponseEntity.ok()::body)
                .orElseGet(ResponseEntity.noContent()::build);
    }
}
