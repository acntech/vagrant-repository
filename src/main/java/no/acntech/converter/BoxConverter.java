package no.acntech.converter;

import io.micrometer.core.lang.NonNull;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.ZoneId;

import no.acntech.model.Box;
import no.acntech.model.Organization;
import no.acntech.model.tables.records.BoxesRecord;

@Component
public class BoxConverter implements Converter<Pair<BoxesRecord, Organization>, Box> {

    @NonNull
    @Override
    public Box convert(@NonNull final Pair<BoxesRecord, Organization> source) {
        final var boxesRecord = source.getLeft();
        final var organization = source.getRight();
        final var tag = organization.name() + "/" + boxesRecord.getName();
        return new Box(
                boxesRecord.getId(),
                tag,
                boxesRecord.getName(),
                organization.name(),
                boxesRecord.getDescription(),
                null,
                null,
                boxesRecord.getPrivate(),
                boxesRecord.getDownloads(),
                boxesRecord.getOrganizationId(),
                boxesRecord.getCreated().atZone(ZoneId.systemDefault()),
                boxesRecord.getModified() == null ? null : boxesRecord.getModified().atZone(ZoneId.systemDefault()),
                null,
                null);
    }
}
