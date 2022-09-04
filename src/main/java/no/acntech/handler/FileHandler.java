package no.acntech.handler;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import no.acntech.exception.FileStorageException;
import no.acntech.properties.ApplicationProperties;

@Component
public class FileHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileHandler.class);

    private final ApplicationProperties applicationProperties;

    public FileHandler(final ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    public long saveFile(MultipartFile file, Path directory, String fileName) {
        if (Files.exists(directory)) {
            LOGGER.info("Directory {} already exists, skipping directory creation", directory);
        } else {
            LOGGER.info("Directory {} does not exist, creating directory", directory);
            try {
                Files.createDirectories(directory);
            } catch (IOException e) {
                throw new FileStorageException("Could not create file directory", e);
            }
        }

        var filePath = directory.resolve(fileName);

        if (Files.exists(filePath)) {
            if (applicationProperties.getFile().getOverwriteExistingFiles()) {
                LOGGER.warn("Overwriting exiting file {}", filePath);
            } else {
                throw new FileStorageException("File already exists");
            }
        }

        try {
            return Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new FileStorageException("Could not create file in file directory", e);
        }
    }

    public Resource readFile(Path directory, String fileName) {
        if (!Files.exists(directory)) {
            throw new FileStorageException("Directory does not exist");
        }

        var filePath = directory.resolve(fileName);

        if (!Files.exists(filePath)) {
            throw new FileStorageException("File does not exist");
        }

        try {
            return new UrlResource(filePath.toUri());
        } catch (MalformedURLException e) {
            throw new FileStorageException("File does not exist", e);
        }
    }

    public List<File> listFiles(Path directory) {
        if (!Files.exists(directory)) {
            throw new FileStorageException("Directory does not exist");
        }

        try (var paths = Files.walk(directory)) {
            return paths
                    .map(Path::toFile)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new FileStorageException("Could not list files in file directory", e);
        }
    }

    public void deleteFile(Path directory, String fileName) {
        if (!Files.exists(directory)) {
            LOGGER.warn("Directory {} does not exist", directory);
            return;
        }

        var filePath = directory.resolve(fileName);

        if (!Files.exists(filePath)) {
            LOGGER.warn("File {} does not exist in directory {}", filePath, directory);
            return;
        }

        try {
            if (!Files.deleteIfExists(filePath)) {
                throw new FileStorageException("Could not delete file");
            }
        } catch (IOException e) {
            throw new FileStorageException("Error while deleting file", e);
        }
    }

    public void deleteDirectory(Path directory) {
        if (!Files.exists(directory)) {
            LOGGER.warn("Directory {} does not exist", directory);
            return;
        }

        try (var paths = Files.walk(directory)) {
            if (!paths
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .allMatch(File::delete)) {
                throw new FileStorageException("Could not delete some content from directory");
            }
        } catch (IOException e) {
            throw new FileStorageException("Error while deleting directory", e);
        }
    }

    public String calculateSha1Checksum(Path directory, String fileName) {
        var filePath = directory.resolve(fileName);

        if (Files.exists(filePath)) {
            try (FileInputStream fileInputStream = new FileInputStream(filePath.toFile())) {
                return DigestUtils.sha1Hex(fileInputStream);
            } catch (IOException e) {
                throw new FileStorageException("Could not read file", e);
            }
        } else {
            throw new FileStorageException("File already exists");
        }
    }
}
