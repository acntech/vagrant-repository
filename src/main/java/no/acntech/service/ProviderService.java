package no.acntech.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import no.acntech.model.ModifyProvider;
import no.acntech.model.Provider;
import no.acntech.model.ProviderFile;
import no.acntech.model.ProviderType;

@Service
public class ProviderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProviderService.class);

    private final FileService fileService;

    public ProviderService(final FileService fileService) {
        this.fileService = fileService;
    }

    public Optional<Provider> get(final Long providerId) {
        LOGGER.info("Get provider with ID {}", providerId);
        return null;
    }

    public List<Provider> find(final Long versionId, final ProviderType providerType) {
        if (providerType == null) {
            LOGGER.info("Find providers by version-ID {}", versionId);
            return null;
        } else {
            LOGGER.info("Find providers by version-ID {} and name {}", versionId, providerType);
            return null;
        }
    }

    @Transactional
    public Provider create(final Long versionId, final ModifyProvider modifyProvider) {
        return null;
    }

    @Transactional
    public Provider update(final Long providerId,
                           final MultipartFile file) {
        return null;
    }

    @Transactional
    public void delete(final Long providerId) {
    }

    public List<ProviderFile> findFiles(final Long providerId) {
        return null;
    }
}
