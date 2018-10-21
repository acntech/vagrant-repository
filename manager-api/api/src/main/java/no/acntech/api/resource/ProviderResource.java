package no.acntech.api.resource;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.acntech.common.model.Provider;
import no.acntech.service.service.ProviderService;

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
}
