package no.acntech.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamSource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.nio.file.Files;
import java.nio.file.Paths;

import no.acntech.model.Storage;
import no.acntech.properties.StorageProperties;

@Validated
@Service
public class StorageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StorageService.class);
    private final StorageProperties applicationProperties;
    private final FileService fileService;
    private final ChecksumService checksumService;
    private final UploadService uploadService;

    public StorageService(final StorageProperties storageProperties,
                          final FileService fileService,
                          final ChecksumService checksumService,
                          final UploadService uploadService) {
        this.applicationProperties = storageProperties;
        this.fileService = fileService;
        this.checksumService = checksumService;
        this.uploadService = uploadService;
    }

    public Resource readFile(@NotBlank final String uid) {
        final var fileName = applicationProperties.getFileName();
        final var uploadPath = Paths.get(applicationProperties.getUploadPath())
                .resolve(uid)
                .toAbsolutePath()
                .normalize();
        LOGGER.info("Loading file {} from path {}", fileName, uploadPath);
        return fileService.readFile(fileName, uploadPath);
    }

    @Transactional
    public Storage saveFile(@NotBlank final String uid,
                            @NotNull final InputStreamSource file) {
        final var fileName = applicationProperties.getFileName();
        final var uploadPath = Paths.get(applicationProperties.getUploadPath())
                .resolve(uid)
                .toAbsolutePath()
                .normalize();
        LOGGER.info("Saving file {} to path {}", fileName, uploadPath);
        final var upload = uploadService.getUpload(uid);
        if (!Files.exists(uploadPath)) {
            fileService.createDirectory(uploadPath);
        }
        final var fileSize = fileService.saveFile(fileName, uploadPath, file);
        final var checksum = checksumService.generateChecksum(uploadPath.resolve(fileName), upload.checksumType());
        return new Storage(uid, fileSize, checksum, upload.checksumType());
    }
}
