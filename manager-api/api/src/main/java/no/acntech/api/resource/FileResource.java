package no.acntech.api.resource;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import no.acntech.service.service.FileService;

@RestController
public class FileResource {

    private final FileService fileService;

    public FileResource(final FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping(path = "providers/{providerId}/upload")
    public void postFile(@PathVariable(name = "providerId") final Long providerId,
                         @RequestParam("file") final MultipartFile file) {
        fileService.uploadFile(providerId, file);
    }
}
