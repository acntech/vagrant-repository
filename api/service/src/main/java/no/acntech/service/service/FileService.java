package no.acntech.service.service;

import no.acntech.common.config.ApplicationProperties;
import no.acntech.common.handler.FileHandler;
import no.acntech.common.model.ChecksumType;
import no.acntech.common.model.ProviderFile;
import no.acntech.common.model.ProviderType;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;

@SuppressWarnings("WeakerAccess")
@Service
public class FileService {

    private final ApplicationProperties applicationProperties;
    private final FileHandler fileHandler;

    public FileService(final ApplicationProperties applicationProperties,
                       final FileHandler fileHandler) {
        this.applicationProperties = applicationProperties;
        this.fileHandler = fileHandler;
    }

    public ProviderFile saveFile(String groupName,
                                 String boxName,
                                 String versionName,
                                 ProviderType providerType,
                                 final MultipartFile file) {
        Path uploadPath = Paths.get(applicationProperties.getFile().getUploadDir())
                .resolve(groupName)
                .resolve(boxName)
                .resolve(versionName)
                .resolve(providerType.getName())
                .toAbsolutePath()
                .normalize();

        String fileName = applicationProperties.getFile().getDefaultFileName();

        long fileSize = fileHandler.saveFile(file, uploadPath, fileName);
        String checksum = fileHandler.calculateSha1Checksum(uploadPath, fileName);

        return ProviderFile.builder()
                .fileName(fileName)
                .fileSize(fileSize)
                .checksumType(ChecksumType.SHA1)
                .checksum(checksum)
                .build();
    }

    public Resource loadFile(String groupName,
                             String boxName,
                             String versionName,
                             ProviderType providerType,
                             String fileName) {
        Path filePath = Paths.get(applicationProperties.getFile().getUploadDir())
                .resolve(groupName)
                .resolve(boxName)
                .resolve(versionName)
                .resolve(providerType.getName())
                .toAbsolutePath()
                .normalize();

        return fileHandler.readFile(filePath, fileName);
    }

    public void deleteFile(String groupName,
                           String boxName,
                           String versionName,
                           ProviderType providerType) {
        String fileName = applicationProperties.getFile().getDefaultFileName();

        Path filePath = Paths.get(applicationProperties.getFile().getUploadDir())
                .resolve(groupName)
                .resolve(boxName)
                .resolve(versionName)
                .resolve(providerType.getName())
                .toAbsolutePath()
                .normalize();

        fileHandler.deleteFile(filePath, fileName);
    }
}
