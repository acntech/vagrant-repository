package no.acntech.api.resource;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import no.acntech.common.model.Box;
import no.acntech.common.model.Group;
import no.acntech.service.service.BoxService;
import no.acntech.service.service.GroupService;

@RequestMapping(path = "groups")
@RestController
public class GroupResource {

    private final GroupService groupService;
    private final BoxService boxService;

    public GroupResource(final GroupService groupService,
                         final BoxService boxService) {
        this.groupService = groupService;
        this.boxService = boxService;
    }

    @GetMapping(path = "{id}")
    public ResponseEntity<Group> get(@PathVariable(name = "id") final Long id) {
        Optional<Group> groupOptional = groupService.get(id);
        return groupOptional.map(ResponseEntity.ok()::body)
                .orElseGet(ResponseEntity.noContent()::build);
    }

    @GetMapping
    public ResponseEntity<List<Group>> find(@RequestParam(name = "name", required = false) final String name) {
        List<Group> groups = groupService.find(name);
        return ResponseEntity.ok(groups);
    }

    @PostMapping
    public ResponseEntity<Group> post(@RequestBody final Group group, UriComponentsBuilder uriBuilder) {
        Group createdGroup = groupService.create(group);
        URI uri = uriBuilder.path("groups/{id}").buildAndExpand(createdGroup.getId()).toUri();
        return ResponseEntity.created(uri).body(createdGroup);
    }

    @GetMapping(path = "{id}/boxes")
    public ResponseEntity<List<Box>> findGroupBoxes(@PathVariable(name = "id") final Long id,
                                                    @RequestParam(name = "name", required = false) final String name) {
        List<Box> boxes = boxService.find(id, name);
        return ResponseEntity.ok(boxes);
    }

    @PostMapping(path = "{id}/boxes")
    public ResponseEntity<Box> post(@PathVariable(name = "id") final Long groupId, @RequestBody final Box box, UriComponentsBuilder uriBuilder) {
        Box createdBox = boxService.create(groupId, box);
        URI uri = uriBuilder.path("boxes/{id}").buildAndExpand(createdBox.getId()).toUri();
        return ResponseEntity.created(uri).body(createdBox);
    }
}
