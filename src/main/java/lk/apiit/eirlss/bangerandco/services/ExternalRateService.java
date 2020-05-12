package lk.apiit.eirlss.bangerandco.services;

import lk.apiit.eirlss.bangerandco.models.ExternalRate;
import lk.apiit.eirlss.bangerandco.repositories.ExternalRateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExternalRateService {
    private final ExternalRateRepository externalRateRepository;

    @Autowired
    public ExternalRateService(ExternalRateRepository externalRateRepository) {
        this.externalRateRepository = externalRateRepository;
    }

    public void createExternalRate(String vehicle, double rate) {
        externalRateRepository.save(new ExternalRate(vehicle, rate));
    }
}
