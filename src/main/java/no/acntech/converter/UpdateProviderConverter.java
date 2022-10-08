package no.acntech.converter;

import io.micrometer.core.lang.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import no.acntech.model.ProviderForm;
import no.acntech.model.UpdateProvider;

@Component
public class UpdateProviderConverter implements Converter<ProviderForm, UpdateProvider> {

    @NonNull
    @Override
    public UpdateProvider convert(@NonNull final ProviderForm source) {
        return new UpdateProvider(
                source.getName(),
                source.getChecksum(),
                source.getChecksumType(),
                StringUtils.isNoneBlank(source.getUrl()) ? source.getUrl() : null);
    }
}
