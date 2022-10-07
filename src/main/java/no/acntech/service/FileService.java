package no.acntech.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import no.acntech.exception.StorageException;

@Validated
@Service
public class FileService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileService.class);

    public long saveFile(@NotBlank final String fileName,
                         @NotNull final Path directory,
                         @NotNull final InputStreamSource file) {
        if (!Files.exists(directory)) {
            throw new StorageException("Directory does not exist");
        }

        final var filePath = directory.resolve(fileName);
        if (Files.exists(filePath)) {
            LOGGER.warn("File {} already exist", filePath);
        }

        try {
            return Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new StorageException("Could not create file in file directory", e);
        }
    }

    public Resource readFile(@NotBlank final String fileName,
                             @NotNull final Path directory) {
        if (!Files.exists(directory)) {
            throw new StorageException("Directory does not exist");
        }

        var filePath = directory.resolve(fileName);

        if (!Files.exists(filePath)) {
            throw new StorageException("File does not exist");
        }

        try {
            return new UrlResource(filePath.toUri());
        } catch (MalformedURLException e) {
            throw new StorageException("File does not exist", e);
        }
    }

    public void deleteFile(@NotBlank final String fileName,
                           @NotNull final Path directory) {
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
                throw new StorageException("Could not delete file");
            }
        } catch (IOException e) {
            throw new StorageException("Error while deleting file", e);
        }
    }

    public List<File> readDirectory(@NotNull final Path directory) {
        if (!Files.exists(directory)) {
            throw new StorageException("Directory does not exist");
        }

        try (var paths = Files.walk(directory)) {
            return paths
                    .map(Path::toFile)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new StorageException("Could not list files in file directory", e);
        }
    }

    public void createDirectory(@NotNull final Path directory) {
        if (Files.exists(directory)) {
            LOGGER.warn("Directory {} already exist", directory);
            return;
        }

        try {
            Files.createDirectories(directory);
        } catch (IOException e) {
            throw new StorageException("Could not create directory", e);
        }
    }

    public void deleteDirectory(@NotNull final Path directory) {
        if (!Files.exists(directory)) {
            LOGGER.warn("Directory {} does not exist", directory);
            return;
        }

        try (var paths = Files.walk(directory)) {
            if (!paths
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .allMatch(File::delete)) {
                throw new StorageException("Could not delete some content from directory");
            }
        } catch (IOException e) {
            throw new StorageException("Error while deleting directory", e);
        }
    }
}
