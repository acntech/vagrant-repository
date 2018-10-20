package no.acntech.api.resource;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import no.acntech.common.model.Group;
import no.acntech.service.service.GroupService;

@RequestMapping(path = "groups")
@RestController
public class GroupResource {

    private final GroupService groupService;

    public GroupResource(final GroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping
    public ResponseEntity<List<Group>> find(@RequestParam(name = "name", required = false) final String name) {
        List<Group> groups = groupService.find(name);
        return ResponseEntity.ok(groups);
    }

    @GetMapping(path = "{id}")
    public ResponseEntity<Group> get(@PathVariable(name = "id") final Long id) {
        Optional<Group> groupOptional = groupService.get(id);
        return groupOptional.map(ResponseEntity.ok()::body)
                .orElseGet(ResponseEntity.noContent()::build);
    }

    @PostMapping
    public ResponseEntity<Group> post(@RequestBody Group group, UriComponentsBuilder uriBuilder) {
        Optional<Group> groupOptional = groupService.save(group);
        return groupOptional.map(g -> {
            URI uri = uriBuilder.path("groups/{id}").buildAndExpand(g.getId()).toUri();
            return ResponseEntity.created(uri).body(g);
        }).orElseGet(ResponseEntity.status(HttpStatus.CONFLICT)::build);
    }
}
