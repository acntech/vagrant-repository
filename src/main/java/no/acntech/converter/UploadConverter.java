package no.acntech.converter;

import io.micrometer.core.lang.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.ZoneId;

import no.acntech.model.Algorithm;
import no.acntech.model.UpdateStatus;
import no.acntech.model.Upload;
import no.acntech.model.tables.records.UploadsRecord;

@Component
public class UploadConverter implements Converter<UploadsRecord, Upload> {

    @NonNull
    @Override
    public Upload convert(@NonNull final UploadsRecord source) {
        return new Upload(
                source.getId(),
                source.getUid(),
                source.getUploadPath(),
                null, // TODO: Handle
                source.getFileSize(),
                source.getChecksum(),
                Algorithm.valueOf(source.getChecksumType()),
                UpdateStatus.valueOf(source.getStatus()),
                source.getProviderId(),
                source.getCreated().atZone(ZoneId.systemDefault()),
                source.getModified() == null ? null : source.getModified().atZone(ZoneId.systemDefault()));
    }
}
