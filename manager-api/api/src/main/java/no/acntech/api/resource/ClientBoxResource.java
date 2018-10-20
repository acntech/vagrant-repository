package no.acntech.api.resource;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.acntech.common.model.ClientBox;
import no.acntech.service.service.ClientBoxService;

@RequestMapping(path = "vagrant/boxes")
@RestController
public class ClientBoxResource {

    private final ClientBoxService clientBoxService;

    public ClientBoxResource(final ClientBoxService clientBoxService) {
        this.clientBoxService = clientBoxService;
    }

    @GetMapping(path = "{groupName}/{boxName}")
    public ResponseEntity<ClientBox> get(@PathVariable(name = "groupName") String groupName,
                                         @PathVariable(name = "boxName") String boxName) {
        Optional<ClientBox> clientBox = clientBoxService.get(groupName, boxName);
        return clientBox.map(ResponseEntity::ok).orElseGet(ResponseEntity.notFound()::build);
    }
}
