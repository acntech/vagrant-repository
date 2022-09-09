package no.acntech.converter;

import io.micrometer.core.lang.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.ZoneId;

import no.acntech.model.Version;
import no.acntech.model.VersionStatus;
import no.acntech.model.tables.records.VersionsRecord;

@Component
public class VersionConverter implements Converter<VersionsRecord, Version> {

    @NonNull
    @Override
    public Version convert(@NonNull final VersionsRecord source) {
        return new Version(
                source.getId(),
                source.getVersion(),
                source.getVersion(), // TODO: What is this?
                source.getDescription(),
                source.getDescription(), // TODO: What is this?
                VersionStatus.valueOf(source.getStatus()),
                source.getBoxId(),
                null,
                null,
                source.getCreated().atZone(ZoneId.systemDefault()),
                source.getModified() == null ? null : source.getModified().atZone(ZoneId.systemDefault()),
                null);
    }
}
