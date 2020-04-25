package lk.apiit.eirlss.bangerandco.services;

import lk.apiit.eirlss.bangerandco.insurance.models.Fraud;
import lk.apiit.eirlss.bangerandco.insurance.repositories.FraudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FraudService {
    private FraudRepository fraudRepository;

    @Autowired
    public FraudService(FraudRepository fraudRepository) {
        this.fraudRepository = fraudRepository;
    }

    public Fraud getFraudByNic(String nic) {
        return fraudRepository.findByNic(nic);
    }
}
