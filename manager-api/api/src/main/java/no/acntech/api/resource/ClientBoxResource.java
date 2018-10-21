package no.acntech.api.resource;

import java.util.Optional;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.acntech.common.model.ClientBox;
import no.acntech.service.service.ClientBoxService;
import no.acntech.service.service.FileService;

@RequestMapping(path = "vagrant/boxes")
@RestController
public class ClientBoxResource {

    private final ClientBoxService clientBoxService;
    private final FileService fileService;

    public ClientBoxResource(final ClientBoxService clientBoxService,
                             final FileService fileService) {
        this.clientBoxService = clientBoxService;
        this.fileService = fileService;
    }

    @GetMapping(path = "{groupName}/{boxName}")
    public ResponseEntity<ClientBox> get(@PathVariable(name = "groupName") String groupName,
                                         @PathVariable(name = "boxName") String boxName) {
        Optional<ClientBox> clientBox = clientBoxService.get(groupName, boxName);
        return clientBox.map(ResponseEntity::ok).orElseGet(ResponseEntity.notFound()::build);
    }

    @GetMapping(path = "{groupName}/{boxName}/{versionName}/{providerName}/{fileName}")
    public ResponseEntity<Resource> getFile(@PathVariable(name = "groupName") String groupName,
                                            @PathVariable(name = "boxName") String boxName,
                                            @PathVariable(name = "versionName") String versionName,
                                            @PathVariable(name = "providerName") String providerName,
                                            @PathVariable(name = "fileName") String fileName) {
        Resource fileResource = fileService.downloadFile(groupName, boxName, versionName, providerName, fileName);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileResource.getFilename() + "\"")
                .body(fileResource);
    }
}
