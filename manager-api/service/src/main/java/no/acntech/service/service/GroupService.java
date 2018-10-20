package no.acntech.service.service;

import javax.validation.Valid;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import no.acntech.common.model.Group;
import no.acntech.service.repository.GroupRepository;

@Service
public class GroupService {

    private final GroupRepository groupRepository;

    public GroupService(final GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    public List<Group> find(final String name) {
        if (name == null) {
            return groupRepository.findAll();
        } else {
            return groupRepository.findByName(name);
        }
    }

    public Optional<Group> get(final Long groupId) {
        return groupRepository.findById(groupId);
    }

    @Transactional
    public Group create(@Valid final Group group) {
        List<Group> groups = groupRepository.findByName(group.getName());
        if (groups.isEmpty()) {
            return Optional.ofNullable(groupRepository.save(group))
                    .orElseThrow(() -> new IllegalStateException("Save returned no value"));
        } else {
            throw new IllegalStateException("A group with name " + group.getName() + " already exists");
        }
    }
}
