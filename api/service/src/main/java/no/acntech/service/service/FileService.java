package no.acntech.service.service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import no.acntech.common.config.ApplicationProperties;
import no.acntech.common.handler.FileHandler;
import no.acntech.common.model.Box;
import no.acntech.common.model.ChecksumType;
import no.acntech.common.model.Group;
import no.acntech.common.model.Provider;
import no.acntech.common.model.Version;
import no.acntech.service.repository.ProviderRepository;

@Service
public class FileService {

    private final ApplicationProperties applicationProperties;
    private final FileHandler fileHandler;
    private final ProviderRepository providerRepository;

    public FileService(final ApplicationProperties applicationProperties,
                       final FileHandler fileHandler,
                       final ProviderRepository providerRepository) {
        this.applicationProperties = applicationProperties;
        this.fileHandler = fileHandler;
        this.providerRepository = providerRepository;
    }

    public Optional<Provider> uploadFile(final Long providerId,
                                         final MultipartFile file) {
        Optional<Provider> providerOptional = providerRepository.findById(providerId);

        if (providerOptional.isPresent()) {
            Provider provider = providerOptional.get();
            Version version = provider.getVersion();
            Box box = version.getBox();
            Group group = box.getGroup();

            Path uploadPath = Paths.get(applicationProperties.getFile().getUploadDir())
                    .resolve(group.getName())
                    .resolve(box.getName())
                    .resolve(version.getName())
                    .resolve(provider.getProviderType().getName())
                    .toAbsolutePath()
                    .normalize();

            String fileName = applicationProperties.getFile().getDefaultFileName();

            long fileSize = fileHandler.saveFile(file, uploadPath, fileName);
            String sha1Checksum = fileHandler.calculateSha1Checksum(uploadPath, fileName);

            provider.setSize(fileSize);
            provider.setChecksumType(ChecksumType.SHA1);
            provider.setChecksum(sha1Checksum);

            return Optional.of(providerRepository.save(provider));
        } else {
            throw new IllegalStateException("No provider found for ID " + providerId);
        }
    }

    public Resource downloadFile(String groupName,
                                 String boxName,
                                 String versionName,
                                 String providerName,
                                 String fileName) {
        Path downloadPath = Paths.get(applicationProperties.getFile().getUploadDir())
                .resolve(groupName)
                .resolve(boxName)
                .resolve(versionName)
                .resolve(providerName)
                .toAbsolutePath()
                .normalize();

        return fileHandler.readFile(downloadPath, fileName);
    }
}
