package no.acntech.api.resource;

import no.acntech.common.model.ClientBox;
import no.acntech.service.service.ClientService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

@RequestMapping(path = "api/vagrant/boxes")
@RestController
public class ClientResource {

    private final ClientService clientService;

    public ClientResource(final ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping(path = "{groupName}/{boxName}")
    public ResponseEntity<ClientBox> get(@PathVariable(name = "groupName") String groupName,
                                         @PathVariable(name = "boxName") String boxName,
                                         final UriComponentsBuilder uriBuilder) {
        Optional<ClientBox> clientBox = clientService.get(groupName, boxName, uriBuilder);
        return clientBox.map(ResponseEntity::ok).orElseGet(ResponseEntity.notFound()::build);
    }

    @GetMapping(path = "{groupName}/{boxName}/{versionName}/{providerName}/{fileName}")
    public ResponseEntity<Resource> getFile(@PathVariable(name = "groupName") String groupName,
                                            @PathVariable(name = "boxName") String boxName,
                                            @PathVariable(name = "versionName") String versionName,
                                            @PathVariable(name = "providerName") String providerName,
                                            @PathVariable(name = "fileName") String fileName) {
        Resource fileResource = clientService.getFile(groupName, boxName, versionName, providerName, fileName);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileResource.getFilename() + "\"")
                .body(fileResource);
    }
}
