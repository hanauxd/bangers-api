package lk.apiit.eirlss.bangerandco.repositories;

import lk.apiit.eirlss.bangerandco.models.ReportedLicense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportedLicenseRepository extends JpaRepository<ReportedLicense, String> {
    ReportedLicense findByLicense(String license);
}
