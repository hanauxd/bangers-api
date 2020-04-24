package lk.apiit.eirlss.bangerandco.insurance.repositories;

import lk.apiit.eirlss.bangerandco.insurance.models.Fraud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FraudRepository extends JpaRepository<Fraud, String> {
    Fraud findByNic(String nic);
    Fraud findByLicense(String license);
}
