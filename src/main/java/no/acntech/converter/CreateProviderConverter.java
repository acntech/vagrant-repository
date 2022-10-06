package no.acntech.converter;

import io.micrometer.core.lang.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import no.acntech.model.CreateProvider;
import no.acntech.model.ProviderForm;

@Component
public class CreateProviderConverter implements Converter<ProviderForm, CreateProvider> {

    @NonNull
    @Override
    public CreateProvider convert(@NonNull final ProviderForm source) {
        return new CreateProvider(
                source.getName(),
                source.getChecksum(),
                source.getChecksumType(),
                StringUtils.isNoneBlank(source.getUrl()) ? source.getUrl() : null);
    }
}
