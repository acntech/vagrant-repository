package no.acntech.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import no.acntech.model.Box;

@Repository
public interface BoxRepository extends JpaRepository<Box, Long> {

    List<Box> findByGroupId(Long groupId);

    List<Box> findByGroupIdAndName(Long groupId, String name);
}
