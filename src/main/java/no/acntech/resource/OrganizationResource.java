package no.acntech.resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import no.acntech.model.AddMember;
import no.acntech.model.CreateOrganization;
import no.acntech.model.Organization;
import no.acntech.model.UpdateOrganization;
import no.acntech.service.MemberService;
import no.acntech.service.OrganizationService;
import no.acntech.service.SecurityService;
import no.acntech.util.UrlBuilder;

@RequestMapping(path = "/api/v1/organization")
@RestController
public class OrganizationResource {

    private final SecurityService securityService;
    private final OrganizationService organizationService;
    private final MemberService memberService;

    public OrganizationResource(final SecurityService securityService,
                                final OrganizationService organizationService,
                                final MemberService memberService) {
        this.securityService = securityService;
        this.organizationService = organizationService;
        this.memberService = memberService;
    }

    @GetMapping(path = "{name}")
    public ResponseEntity<Organization> getOrganization(@PathVariable(name = "name") final String name) {
        final var organization = organizationService.getOrganization(name);
        return ResponseEntity.ok(organization);
    }

    @PostMapping
    public ResponseEntity<Organization> createOrganization(@RequestBody @Valid @NotNull final CreateOrganization.Request createOrganizationRequest,
                                                           final UriComponentsBuilder uriBuilder) {
        final var createOrganization = createOrganizationRequest.organization();
        final var username = securityService.getUsername();
        organizationService.createOrganization(username, createOrganization);
        final var uri = UrlBuilder.organizationUri(uriBuilder, createOrganization.name());
        return ResponseEntity.created(uri).build();
    }

    @PutMapping(path = "{name}")
    public ResponseEntity<Organization> updateOrganization(@PathVariable(name = "name") final String name,
                                                           @RequestBody @Valid @NotNull final UpdateOrganization.Request updateOrganizationRequest) {
        final var updateOrganization = updateOrganizationRequest.organization();
        organizationService.updateOrganization(name, updateOrganization);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(path = "{name}")
    public ResponseEntity<Void> deleteOrganization(@PathVariable(name = "name") final String name) {
        organizationService.deleteOrganization(name);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(path = "{name}/user")
    public ResponseEntity<Organization> addOrganizationMember(@PathVariable(name = "name") final String name,
                                                              @RequestBody @Valid @NotNull final AddMember.Request addMemberRequest,
                                                              final UriComponentsBuilder uriBuilder) {
        final var addMember = addMemberRequest.user();
        final var uri = UrlBuilder.organizationMemberUri(uriBuilder, name, addMember.username());
        memberService.addMember(name, addMember);
        return ResponseEntity.created(uri).build();
    }

    @DeleteMapping(path = "{name}/user/{username}")
    public ResponseEntity<Organization> removeOrganizationMember(@PathVariable(name = "name") final String name,
                                                                 @PathVariable(name = "username") final String username) {
        memberService.removeMember(name, username);
        return ResponseEntity.ok().build();
    }
}
