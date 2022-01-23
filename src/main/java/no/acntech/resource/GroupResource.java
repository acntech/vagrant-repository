package no.acntech.resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import no.acntech.model.Box;
import no.acntech.model.Group;
import no.acntech.model.ModifyBox;
import no.acntech.model.ModifyGroup;
import no.acntech.service.BoxService;
import no.acntech.service.GroupService;

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
    public ResponseEntity<Group> get(@PathVariable(name = "id") final Long groupId) {
        Optional<Group> groupOptional = groupService.get(groupId);
        return groupOptional.map(ResponseEntity.ok()::body)
                .orElseGet(ResponseEntity.noContent()::build);
    }

    @GetMapping
    public ResponseEntity<List<Group>> find(@RequestParam(name = "name", required = false) final String name) {
        List<Group> groups = groupService.find(name);
        return ResponseEntity.ok(groups);
    }

    @PostMapping
    public ResponseEntity<Group> post(@Valid @RequestBody final ModifyGroup modifyGroup,
                                      final UriComponentsBuilder uriBuilder) {
        Group createdGroup = groupService.create(modifyGroup);
        URI uri = uriBuilder.path("groups/{id}").buildAndExpand(createdGroup.getId()).toUri();
        return ResponseEntity.created(uri).body(createdGroup);
    }

    @DeleteMapping(path = "{id}")
    public ResponseEntity delete(@PathVariable(name = "id") final Long groupId) {
        groupService.delete(groupId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(path = "{id}")
    public ResponseEntity<Group> put(@PathVariable(name = "id") final Long groupId,
                                     @Valid @RequestBody final ModifyGroup modifyGroup) {
        Group group = groupService.update(groupId, modifyGroup);
        return ResponseEntity.ok(group);
    }

    @GetMapping(path = "{id}/boxes")
    public ResponseEntity<List<Box>> findGroupBoxes(@PathVariable(name = "id") final Long groupId,
                                                    @RequestParam(name = "name", required = false) final String name) {
        List<Box> boxes = boxService.find(groupId, name);
        return ResponseEntity.ok(boxes);
    }

    @PostMapping(path = "{id}/boxes")
    public ResponseEntity<Box> post(@PathVariable(name = "id") final Long groupId,
                                    @Valid @RequestBody final ModifyBox modifyBox,
                                    final UriComponentsBuilder uriBuilder) {
        Box createdBox = boxService.create(groupId, modifyBox);
        URI uri = uriBuilder.path("boxes/{id}").buildAndExpand(createdBox.getId()).toUri();
        return ResponseEntity.created(uri).body(createdBox);
    }
}
