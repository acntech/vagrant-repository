package no.acntech.service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import no.acntech.common.model.Provider;
import no.acntech.common.model.ProviderType;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, Long> {

    List<Provider> findByVersionId(Long versionId);

    List<Provider> findByVersionIdAndType(Long versionId, ProviderType type);
}
