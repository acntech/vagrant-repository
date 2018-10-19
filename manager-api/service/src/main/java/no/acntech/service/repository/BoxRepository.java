package no.acntech.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import no.acntech.common.model.Box;

@Repository
public interface BoxRepository extends JpaRepository<Box, Long> {

}
