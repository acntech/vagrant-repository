package no.acntech.converter;

import io.micrometer.core.lang.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import no.acntech.model.CreateOrganization;
import no.acntech.model.OrganizationForm;

@Component
public class CreateOrganizationConverter implements Converter<OrganizationForm, CreateOrganization> {

    @NonNull
    @Override
    public CreateOrganization convert(@NonNull final OrganizationForm source) {
        return new CreateOrganization(
                source.getName(),
                source.getDescription(),
                source.getPrivate());
    }
}
