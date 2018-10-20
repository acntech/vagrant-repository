package no.acntech.service.service;

import javax.validation.Valid;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import no.acntech.common.model.Box;
import no.acntech.common.model.Group;
import no.acntech.service.repository.BoxRepository;
import no.acntech.service.repository.GroupRepository;

@Service
public class BoxService {

    private final BoxRepository boxRepository;
    private final GroupRepository groupRepository;

    public BoxService(final BoxRepository boxRepository,
                      final GroupRepository groupRepository) {
        this.boxRepository = boxRepository;
        this.groupRepository = groupRepository;
    }

    public Optional<Box> get(final Long boxId) {
        return boxRepository.findById(boxId);
    }

    public List<Box> find(final Long groupId, final String name) {
        if (groupId == null) {
            throw new IllegalArgumentException("Group ID is null");
        } else {
            if (name == null) {
                return boxRepository.findByGroupId(groupId);
            } else {
                return boxRepository.findByGroupIdAndName(groupId, name);
            }
        }
    }

    @Transactional
    public Box create(final Long groupId, @Valid final Box box) {
        Optional<Group> groupOptional = groupRepository.findById(groupId);
        if (groupOptional.isPresent()) {
            Group group = groupOptional.get();
            List<Box> boxes = boxRepository.findByGroupId(group.getId());
            if (boxes.stream().anyMatch(b -> b.getName().equals(box.getName()))) {
                throw new IllegalStateException("A box with name " + box.getName() + " already exists for group " + group.getName());
            } else {
                Box saveBox = Box.builder()
                        .from(box)
                        .group(group)
                        .build();
                return Optional.ofNullable(boxRepository.save(saveBox))
                        .orElseThrow(() -> new IllegalStateException("Save returned no value"));
            }
        } else {
            throw new IllegalStateException("A group with ID " + groupId + " does not exist");
        }
    }
}
