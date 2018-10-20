package no.acntech.api.resource;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.acntech.common.model.Box;
import no.acntech.service.service.BoxService;

@RequestMapping(path = "boxes")
@RestController
public class BoxResource {

    private final BoxService boxService;

    public BoxResource(final BoxService boxService) {
        this.boxService = boxService;
    }

    @GetMapping(path = "{id}")
    public ResponseEntity<Box> get(@PathVariable(name = "id") final Long id) {
        Optional<Box> boxOptional = boxService.get(id);
        return boxOptional.map(ResponseEntity.ok()::body)
                .orElseGet(ResponseEntity.noContent()::build);
    }
}
