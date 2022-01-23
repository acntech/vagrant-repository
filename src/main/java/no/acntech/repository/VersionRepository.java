package no.acntech.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import no.acntech.model.Version;

@Repository
public interface VersionRepository extends JpaRepository<Version, Long> {

    List<Version> findByBoxId(Long boxId);

    List<Version> findByBoxIdAndName(Long boxId, String name);
}
