package no.acntech.service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import no.acntech.common.model.Box;

@Repository
public interface BoxRepository extends JpaRepository<Box, Long> {

    List<Box> findByGroupId(Long groupId);

    List<Box> findByGroupIdAndName(Long groupId, String name);
}
