package no.acntech.api.resource;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public List<Group> find() {
        return groupService.find();
    }
}
