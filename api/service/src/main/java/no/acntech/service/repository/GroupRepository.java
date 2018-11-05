package no.acntech.service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import no.acntech.common.model.Group;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

    List<Group> findByName(String name);
}
