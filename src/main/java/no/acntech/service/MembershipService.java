package no.acntech.service;

import org.jooq.exception.NoDataFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotBlank;
import java.util.Arrays;

import no.acntech.model.MemberRole;
import no.acntech.repository.MemberRepository;

@Service
public class MembershipService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MembershipService.class);
    private final MemberRepository memberRepository;

    public MembershipService(final MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public boolean hasMembership(@NotBlank final String name,
                                 @NotBlank final String username) {
        try {
            memberRepository.getMember(name, username);
            return true;
        } catch (NoDataFoundException e) {
            return false;
        }
    }

    public boolean hasAnyRoles(@NotBlank final String name,
                               @NotBlank final String username,
                               final MemberRole... roles) {
        try {
            final var member = memberRepository.getMember(name, username);
            return roles.length > 0 && Arrays.stream(roles)
                    .map(MemberRole::name)
                    .anyMatch(role -> role.equals(member.getRole()));
        } catch (NoDataFoundException e) {
            return false;
        }
    }
}
