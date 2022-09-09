package no.acntech.resource;

import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import no.acntech.service.StorageService;
import no.acntech.service.UploadService;

@RequestMapping(path = "/api/storage")
@RestController
public class StorageResource {

    private final UploadService uploadService;
    private final StorageService storageService;

    public StorageResource(final UploadService uploadService,
                           final StorageService storageService) {
        this.uploadService = uploadService;
        this.storageService = storageService;
    }

    @GetMapping(path = "{uid}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> getObject(@PathVariable(name = "uid") final String uid) {
        final var resource = storageService.readFile(uid);
        return ResponseEntity.ok(resource);
    }

    @PostMapping(path = "{uid}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadObject(@PathVariable(name = "uid") final String uid,
                                             @RequestParam("file") MultipartFile file) {
        final var storage = storageService.saveFile(uid, file);
        uploadService.updateUpload(storage.uid(), storage.fileSize(), storage.checksum());
        uploadService.verifyUpload(uid);
        return ResponseEntity.ok().build();
    }
}
