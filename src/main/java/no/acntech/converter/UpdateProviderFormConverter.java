package no.acntech.converter;

import io.micrometer.core.lang.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import no.acntech.model.Provider;
import no.acntech.model.ProviderForm;

@Component
public class UpdateProviderFormConverter implements Converter<Provider, ProviderForm> {

    @NonNull
    @Override
    public ProviderForm convert(@NonNull final Provider source) {
        return new ProviderForm(
                source.name(),
                source.checksum(),
                source.checksumType(),
                source.originalUrl());
    }
}
