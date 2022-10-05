package no.acntech.converter;

import io.micrometer.core.lang.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import no.acntech.model.OrganizationForm;
import no.acntech.model.UpdateOrganization;

@Component
public class UpdateOrganizationConverter implements Converter<OrganizationForm, UpdateOrganization> {

    @NonNull
    @Override
    public UpdateOrganization convert(@NonNull final OrganizationForm source) {
        return new UpdateOrganization(
                source.getName(),
                source.getDescription(),
                source.getPrivate());
    }
}
