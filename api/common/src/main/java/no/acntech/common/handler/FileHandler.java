package no.acntech.common.handler;

import no.acntech.common.exception.FileStorageException;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class FileHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileHandler.class);

    public long saveFile(MultipartFile file, Path fileDirectory, String fileName) {
        if (Files.exists(fileDirectory)) {
            LOGGER.info("File directory already exists, skipping directory creation");
        } else {
            LOGGER.info("File directory does not exist, creating directory");
            try {
                Files.createDirectories(fileDirectory);
            } catch (IOException e) {
                throw new FileStorageException("Could not create file directory", e);
            }
        }

        Path filePath = fileDirectory.resolve(fileName);

        if (Files.exists(filePath)) {
            throw new FileStorageException("File already exists");
        }

        try {
            return Files.copy(file.getInputStream(), filePath);
        } catch (IOException e) {
            throw new FileStorageException("Could not create file in file directory", e);
        }
    }

    public Resource readFile(Path fileDirectory, String fileName) {
        if (!Files.exists(fileDirectory)) {
            throw new FileStorageException("File directory does not exist");
        }

        Path filePath = fileDirectory.resolve(fileName);

        if (!Files.exists(filePath)) {
            throw new FileStorageException("File does not exist");
        }

        try {
            return new UrlResource(filePath.toUri());
        } catch (MalformedURLException e) {
            throw new FileStorageException("File does not exist", e);
        }
    }

    public void deleteFile(Path fileDirectory, String fileName) {
        if (!Files.exists(fileDirectory)) {
            throw new FileStorageException("File directory does not exist");
        }

        Path filePath = fileDirectory.resolve(fileName);

        if (!Files.exists(filePath)) {
            throw new FileStorageException("File does not exist");
        }

        try {
            if (!Files.deleteIfExists(filePath)) {
                throw new FileStorageException("Could not delete file");
            }
        } catch (IOException e) {
            throw new FileStorageException("Error while deleting file", e);
        }
    }

    public String calculateSha1Checksum(Path fileDirectory, String fileName) {
        Path filePath = fileDirectory.resolve(fileName);

        if (Files.exists(filePath)) {
            try {
                byte[] content = Files.readAllBytes(filePath);
                return DigestUtils.sha1Hex(content);
            } catch (IOException e) {
                throw new FileStorageException("Could not read file", e);
            }
        } else {
            throw new FileStorageException("File already exists");
        }
    }
}
