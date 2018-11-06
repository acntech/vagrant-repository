package no.acntech.service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import no.acntech.common.model.Version;

@Repository
public interface VersionRepository extends JpaRepository<Version, Long> {

    List<Version> findByBoxId(Long boxId);

    List<Version> findByBoxIdAndName(Long boxId, String name);
}
