package no.acntech.converter;

import io.micrometer.core.lang.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.ZoneId;

import no.acntech.model.Organization;
import no.acntech.model.tables.records.OrganizationsRecord;

@Component
public class OrganizationConverter implements Converter<OrganizationsRecord, Organization> {

    @NonNull
    @Override
    public Organization convert(@NonNull final OrganizationsRecord source) {
        return new Organization(
                source.getId(),
                source.getName(),
                source.getDescription(),
                source.getPrivate(),
                source.getCreated().atZone(ZoneId.systemDefault()),
                source.getModified() == null ? null : source.getModified().atZone(ZoneId.systemDefault()));
    }
}
