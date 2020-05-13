package lk.apiit.eirlss.bangerandco.services;

import lk.apiit.eirlss.bangerandco.models.ExternalRate;
import lk.apiit.eirlss.bangerandco.repositories.ExternalRateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<ExternalRate> getVehiclesContaining(String vehicle) {
        return externalRateRepository.findByVehicleContaining(vehicle);
    }

    public void deleteAllInBatch() {
        externalRateRepository.deleteAllInBatch();
    }

    public double average(String vehicle) {
        List<ExternalRate> externalRates = getVehiclesContaining(vehicle);
        double rate = 0;
        for (ExternalRate extRate : externalRates) {
            rate += extRate.getRate();
        }
        return rate / externalRates.size();
    }
}
