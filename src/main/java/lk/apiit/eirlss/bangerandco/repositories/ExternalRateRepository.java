package lk.apiit.eirlss.bangerandco.repositories;

import lk.apiit.eirlss.bangerandco.models.ExternalRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExternalRateRepository extends JpaRepository<ExternalRate, String> {
}
