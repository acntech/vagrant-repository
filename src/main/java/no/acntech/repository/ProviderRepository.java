package no.acntech.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import no.acntech.model.Provider;
import no.acntech.model.ProviderType;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, Long> {

    List<Provider> findByVersionId(Long versionId);

    List<Provider> findByVersionIdAndProviderType(Long versionId, ProviderType providerType);
}
