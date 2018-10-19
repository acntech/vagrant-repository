package no.acntech.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import no.acntech.common.model.Provider;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, Long> {

}
