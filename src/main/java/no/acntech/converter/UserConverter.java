package no.acntech.converter;

import io.micrometer.core.lang.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.ZoneId;

import no.acntech.model.User;
import no.acntech.model.UserRole;
import no.acntech.model.tables.records.UsersRecord;

@Component
public class UserConverter implements Converter<UsersRecord, User> {

    @NonNull
    @Override
    public User convert(@NonNull final UsersRecord source) {
        return new User(
                source.getId(),
                source.getUsername(),
                UserRole.valueOf(source.getRole()),
                source.getPasswordHash(),
                source.getCreated().atZone(ZoneId.systemDefault()),
                source.getModified() == null ? null : source.getModified().atZone(ZoneId.systemDefault()));
    }
}
