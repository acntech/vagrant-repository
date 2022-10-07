package no.acntech.converter;

import io.micrometer.core.lang.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.ZoneId;

import no.acntech.model.Algorithm;
import no.acntech.model.Provider;
import no.acntech.model.ProviderStatus;
import no.acntech.model.ProviderType;
import no.acntech.model.tables.records.ProvidersRecord;

@Component
public class ProviderConverter implements Converter<ProvidersRecord, Provider> {

    @NonNull
    @Override
    public Provider convert(@NonNull final ProvidersRecord source) {
        return new Provider(
                source.getId(),
                ProviderType.valueOf(source.getName()),
                source.getChecksum(),
                Algorithm.valueOf(source.getChecksumType()),
                source.getHosted(),
                source.getHostedToken(),
                ProviderStatus.valueOf(source.getStatus()),
                source.getVersionId(),
                source.getOriginalUrl(),
                null, // Is set in resource
                source.getCreated().atZone(ZoneId.systemDefault()),
                source.getModified() == null ? null : source.getModified().atZone(ZoneId.systemDefault()));
    }
}
