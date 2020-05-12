package lk.apiit.eirlss.bangerandco.services;

import lk.apiit.eirlss.bangerandco.models.ReportedLicense;
import lk.apiit.eirlss.bangerandco.repositories.ReportedLicenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportedLicenseService {
    private final ReportedLicenseRepository licenseRepository;

    @Autowired
    public ReportedLicenseService(ReportedLicenseRepository licenseRepository) {
        this.licenseRepository = licenseRepository;
    }

    public ReportedLicense getByLicense(String license) {
        return licenseRepository.findByLicense(license);
    }

    public void deleteAllInBatch() {
        licenseRepository.deleteAllInBatch();
    }

    public void saveAllInBatch(List<ReportedLicense> licenses) {
        licenseRepository.saveAll(licenses);
    }
}
