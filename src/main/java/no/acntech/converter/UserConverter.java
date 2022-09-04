package no.acntech.converter;

import io.micrometer.core.lang.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.ZoneId;

import no.acntech.model.Role;
import no.acntech.model.User;
import no.acntech.model.tables.records.UsersRecord;

@Component
public class UserConverter implements Converter<UsersRecord, User> {

    @NonNull
    @Override
    public User convert(@NonNull final UsersRecord source) {
        return User.builder()
                .id(source.getId())
                .username(source.getUsername())
                .name(source.getName())
                .role(Role.valueOf(source.getRole()))
                .passwordHash(source.getPasswordHash())
                .passwordSalt(source.getPasswordSalt())
                .created(source.getCreated().atZone(ZoneId.systemDefault()))
                .build();
    }
}
