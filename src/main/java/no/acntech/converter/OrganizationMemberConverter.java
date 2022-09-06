package no.acntech.converter;

import io.micrometer.core.lang.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import no.acntech.model.OrganizationMember;
import no.acntech.model.OrganizationRole;

import static no.acntech.model.tables.Members.MEMBERS;

@Component
public class OrganizationMemberConverter implements Converter<org.jooq.Record, OrganizationMember> {

    @NonNull
    @Override
    public OrganizationMember convert(@NonNull final org.jooq.Record source) {
        return new OrganizationMember(
                source.get(MEMBERS.ID),
                source.get(MEMBERS.ORGANIZATION_ID),
                source.get(MEMBERS.USER_ID),
                OrganizationRole.valueOf(source.get(MEMBERS.ROLE)));
    }
}
