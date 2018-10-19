package no.acntech.service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import no.acntech.common.model.Group;
import no.acntech.service.repository.GroupRepository;

@Service
public class GroupService {

    private final GroupRepository groupRepository;

    public GroupService(final GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    public List<Group> find() {
        return groupRepository.findAll();
    }
}
