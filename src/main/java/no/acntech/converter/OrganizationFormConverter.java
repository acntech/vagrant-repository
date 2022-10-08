package no.acntech.converter;

import io.micrometer.core.lang.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import no.acntech.model.Organization;
import no.acntech.model.OrganizationForm;

@Component
public class OrganizationFormConverter implements Converter<Organization, OrganizationForm> {

    @NonNull
    @Override
    public OrganizationForm convert(@NonNull final Organization source) {
        return new OrganizationForm(
                source.name(),
                source.description(),
                source.isPrivate());
    }
}
