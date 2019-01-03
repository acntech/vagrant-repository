package no.acntech.service.service;

import no.acntech.common.config.ApplicationProperties;
import no.acntech.common.handler.FileHandler;
import no.acntech.common.model.ChecksumType;
import no.acntech.common.model.ProviderFile;
import no.acntech.common.model.ProviderType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("WeakerAccess")
@Service
public class FileService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileService.class);

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

        LOGGER.info("Saving file {} to path {}", fileName, uploadPath);

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

        LOGGER.info("Loading file {} from path {}", fileName, filePath);

        return fileHandler.readFile(filePath, fileName);
    }

    public void deleteDirectory(String groupName) {

        Path filePath = Paths.get(applicationProperties.getFile().getUploadDir())
                .resolve(groupName)
                .toAbsolutePath()
                .normalize();

        LOGGER.info("Deleting directory from path {}", filePath);

        fileHandler.deleteDirectory(filePath);
    }

    public void deleteDirectory(String groupName,
                                String boxName) {

        Path filePath = Paths.get(applicationProperties.getFile().getUploadDir())
                .resolve(groupName)
                .resolve(boxName)
                .toAbsolutePath()
                .normalize();

        LOGGER.info("Deleting directory from path {}", filePath);

        fileHandler.deleteDirectory(filePath);
    }

    public void deleteDirectory(String groupName,
                                String boxName,
                                String versionName) {

        Path filePath = Paths.get(applicationProperties.getFile().getUploadDir())
                .resolve(groupName)
                .resolve(boxName)
                .resolve(versionName)
                .toAbsolutePath()
                .normalize();

        LOGGER.info("Deleting directory from path {}", filePath);

        fileHandler.deleteDirectory(filePath);
    }

    public void deleteDirectory(String groupName,
                                String boxName,
                                String versionName,
                                ProviderType providerType) {

        Path filePath = Paths.get(applicationProperties.getFile().getUploadDir())
                .resolve(groupName)
                .resolve(boxName)
                .resolve(versionName)
                .resolve(providerType.getName())
                .toAbsolutePath()
                .normalize();

        LOGGER.info("Deleting directory from path {}", filePath);

        fileHandler.deleteDirectory(filePath);
    }

    public List<ProviderFile> findDirectoryFiles(String groupName,
                                                 String boxName,
                                                 String versionName,
                                                 ProviderType providerType) {

        Path filePath = Paths.get(applicationProperties.getFile().getUploadDir())
                .resolve(groupName)
                .resolve(boxName)
                .resolve(versionName)
                .resolve(providerType.getName())
                .toAbsolutePath()
                .normalize();

        LOGGER.info("Find directory files from path {}", filePath);

        List<File> files = fileHandler.listFiles(filePath);

        return files.stream()
                .filter(File::isFile)
                .map(file -> ProviderFile.builder()
                        .providerType(providerType)
                        .fileName(file.getName())
                        .fileSize(file.length())
                        .build())
                .collect(Collectors.toList());
    }
}
