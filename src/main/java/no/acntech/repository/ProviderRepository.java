package no.acntech.repository;

import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import no.acntech.model.Algorithm;
import no.acntech.model.ProviderStatus;
import no.acntech.model.ProviderType;
import no.acntech.model.tables.records.ProvidersRecord;

import static no.acntech.model.tables.Providers.PROVIDERS;

@Repository
public class ProviderRepository {

    private final DSLContext context;

    public ProviderRepository(final DSLContext context) {
        this.context = context;
    }

    public ProvidersRecord getProvider(final Integer id) {
        try (final var select = context.selectFrom(PROVIDERS)) {
            return select
                    .where(PROVIDERS.ID.eq(id))
                    .fetchSingle();
        }
    }

    public ProvidersRecord getProvider(final ProviderType provider,
                                       final Integer versionId) {
        try (final var select = context.selectFrom(PROVIDERS)) {
            return select
                    .where(PROVIDERS.NAME.eq(provider.name()))
                    .and(PROVIDERS.VERSION_ID.eq(versionId))
                    .fetchSingle();
        }
    }

    public Result<ProvidersRecord> findProviders(final Integer versionId) {
        try (final var select = context.selectFrom(PROVIDERS)) {
            return select
                    .where(PROVIDERS.VERSION_ID.eq(versionId))
                    .fetch();
        }
    }

    @Transactional
    public int createProvider(final ProviderType name,
                              final String checksum,
                              final Algorithm checksumType,
                              final String url,
                              final ProviderStatus status,
                              final Integer versionId) {
        try (final var insert = context
                .insertInto(PROVIDERS,
                        PROVIDERS.NAME,
                        PROVIDERS.CHECKSUM,
                        PROVIDERS.CHECKSUM_TYPE,
                        PROVIDERS.HOSTED,
                        PROVIDERS.ORIGINAL_URL,
                        PROVIDERS.STATUS,
                        PROVIDERS.VERSION_ID,
                        PROVIDERS.CREATED)) {
            return insert
                    .values(
                            name.name(),
                            checksum,
                            checksumType.name(),
                            StringUtils.isBlank(url),
                            url,
                            status.name(),
                            versionId,
                            LocalDateTime.now())
                    .execute();
        }
    }

    @Transactional
    public int updateProvider(final ProviderType name,
                              final String checksum,
                              final Algorithm checksumType,
                              final String url,
                              final ProviderStatus status,
                              final Integer providerId) {
        try (final var update = context
                .update(PROVIDERS)
                .set(PROVIDERS.NAME, name.name())
                .set(PROVIDERS.CHECKSUM, checksum)
                .set(PROVIDERS.CHECKSUM_TYPE, checksumType.name())
                .set(PROVIDERS.HOSTED, StringUtils.isBlank(url))
                .set(PROVIDERS.ORIGINAL_URL, url)
                .set(PROVIDERS.STATUS, status.name())
                .set(PROVIDERS.MODIFIED, LocalDateTime.now())) {
            return update
                    .where(PROVIDERS.ID.eq(providerId))
                    .execute();
        }
    }

    @Transactional
    public int deleteProvider(final Integer providerId) {
        try (final var delete = context.deleteFrom(PROVIDERS)) {
            return delete
                    .where(PROVIDERS.ID.eq(providerId))
                    .execute();
        }
    }

    @Transactional
    public int updateProviderStatus(final Integer providerId,
                                    final ProviderStatus status) {
        try (final var update = context
                .update(PROVIDERS)
                .set(PROVIDERS.STATUS, status.name())
                .set(PROVIDERS.MODIFIED, LocalDateTime.now())) {
            return update
                    .where(PROVIDERS.ID.eq(providerId))
                    .execute();
        }
    }
}
