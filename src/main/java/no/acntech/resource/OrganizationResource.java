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

import no.acntech.model.AddOrganizationMember;
import no.acntech.model.CreateOrganization;
import no.acntech.model.Organization;
import no.acntech.model.UpdateOrganization;
import no.acntech.service.OrganizationService;

@RequestMapping(path = "/api/organization")
@RestController
public class OrganizationResource {

    private final OrganizationService organizationService;

    public OrganizationResource(final OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @GetMapping(path = "{name}")
    public ResponseEntity<Organization> getOrganization(@PathVariable(name = "name") final String name) {
        final var organization = organizationService.getOrganization(name);
        return ResponseEntity.ok(organization);
    }

    @PostMapping
    public ResponseEntity<Organization> createOrganization(@RequestBody final CreateOrganization.Request createOrganizationRequest,
                                                           final UriComponentsBuilder uriBuilder) {
        final var createOrganization = createOrganizationRequest.organization();
        organizationService.createOrganization(createOrganization);
        final var uri = uriBuilder.path("/api/organization/{name}")
                .buildAndExpand(createOrganization.name().toLowerCase())
                .toUri();
        return ResponseEntity.created(uri).build();
    }

    @PutMapping(path = "{name}")
    public ResponseEntity<Organization> updateOrganization(@PathVariable(name = "name") final String name,
                                                           @RequestBody final UpdateOrganization.Request updateOrganizationRequest) {
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
                                                              @RequestBody final AddOrganizationMember.Request addOrganizationMemberRequest,
                                                              final UriComponentsBuilder uriBuilder) {
        final var addOrganizationMember = addOrganizationMemberRequest.organization();
        final var uri = uriBuilder.path("/api/organization/{name}/user/{username}")
                .buildAndExpand(name.toLowerCase(), addOrganizationMember.username().toLowerCase())
                .toUri();
        organizationService.addOrganizationMember(name, addOrganizationMember);
        return ResponseEntity.created(uri).build();
    }

    @DeleteMapping(path = "{name}/user/{username}")
    public ResponseEntity<Organization> removeOrganizationMember(@PathVariable(name = "name") final String name,
                                                                 @PathVariable(name = "username") final String username) {
        organizationService.removeOrganizationMember(name, username);
        return ResponseEntity.ok().build();
    }
}
