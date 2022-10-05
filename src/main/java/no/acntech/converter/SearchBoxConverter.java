package no.acntech.converter;

import io.micrometer.core.lang.NonNull;
import org.jooq.Record;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.ZoneId;

import no.acntech.model.Box;

import static no.acntech.model.tables.Boxes.BOXES;
import static no.acntech.model.tables.Organizations.ORGANIZATIONS;

@Component
public class SearchBoxConverter implements Converter<Record, Box> {

    @NonNull
    @Override
    public Box convert(@NonNull final Record source) {
        final var username = source.get(ORGANIZATIONS.NAME);
        final var name = source.get(BOXES.NAME);
        final var tag = username + "/" + name;
        final var created = source.get(BOXES.CREATED);
        final var modified = source.get(BOXES.MODIFIED);
        return new Box(
                source.get(BOXES.ID),
                tag,
                name,
                username,
                source.get(BOXES.DESCRIPTION),
                null, // TODO: Handle
                null, // TODO: Handle
                source.get(BOXES.PRIVATE),
                source.get(BOXES.DOWNLOADS),
                source.get(ORGANIZATIONS.ID),
                created.atZone(ZoneId.systemDefault()),
                modified == null ? null : modified.atZone(ZoneId.systemDefault()),
                null, /// Is set in resource
                null); // Is set in resource
    }
}
