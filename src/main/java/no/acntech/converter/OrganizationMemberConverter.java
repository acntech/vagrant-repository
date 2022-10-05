package no.acntech.converter;

import io.micrometer.core.lang.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import no.acntech.model.Member;
import no.acntech.model.MemberRole;

import static no.acntech.model.tables.Members.MEMBERS;

@Component
public class OrganizationMemberConverter implements Converter<org.jooq.Record, Member> {

    @NonNull
    @Override
    public Member convert(@NonNull final org.jooq.Record source) {
        return new Member(
                source.get(MEMBERS.ID),
                source.get(MEMBERS.ORGANIZATION_ID),
                source.get(MEMBERS.USER_ID),
                MemberRole.valueOf(source.get(MEMBERS.ROLE)));
    }
}
