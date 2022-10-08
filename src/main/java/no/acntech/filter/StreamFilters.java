package no.acntech.filter;

import no.acntech.model.Provider;
import no.acntech.model.ProviderStatus;
import no.acntech.model.tables.records.ProvidersRecord;

public final class StreamFilters {

    private StreamFilters() {
    }

    public static boolean hasStatusNoneVerified(final Provider provider) {
        final var status = provider.status();
        return ProviderStatus.PENDING.equals(status) || ProviderStatus.UPLOADED.equals(status);
    }

    public static boolean hasStatusNonVerified(final ProvidersRecord providersRecord) {
        final var status = ProviderStatus.valueOf(providersRecord.getStatus());
        return ProviderStatus.PENDING.equals(status) || ProviderStatus.UPLOADED.equals(status);
    }
}
