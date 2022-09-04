package no.acntech.service;

import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import no.acntech.model.ModifyVersion;
import no.acntech.model.Version;

@Service
public class VersionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(VersionService.class);

    private final DSLContext dsl;
    private final FileService fileService;

    public VersionService(final DSLContext dsl,
                          final FileService fileService) {
        this.dsl = dsl;
        this.fileService = fileService;
    }

    public Optional<Version> get(final Long versionId) {
        LOGGER.info("Get version with ID {}", versionId);
        return null;
    }

    public List<Version> find(final Long boxId, final String name) {
        return null;
    }

    @Transactional
    public Version create(final Long boxId, final ModifyVersion modifyVersion) {
        return null;
    }

    @Transactional
    public void delete(final Long versionId) {

    }

    @Transactional
    public Version update(final Long versionId,
                          final ModifyVersion modifyVersion) {
        return null;
    }
}
