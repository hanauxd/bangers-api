package lk.apiit.eirlss.bangerandco.services;

import lk.apiit.eirlss.bangerandco.exceptions.BadRequestException;
import lk.apiit.eirlss.bangerandco.exceptions.ResourceNotFoundException;
import lk.apiit.eirlss.bangerandco.models.Utility;
import lk.apiit.eirlss.bangerandco.repositories.UtilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UtilityService {
    private UtilityRepository utilityRepository;

    @Autowired
    public UtilityService(UtilityRepository utilityRepository) {
        this.utilityRepository = utilityRepository;
    }

    public Utility createUtility(Utility newUtility) {
        Utility utility = utilityRepository.findByUtilityType(newUtility.getUtilityType());
        if (utility != null) throw new BadRequestException("Utility type '" + newUtility.getUtilityType() + "' already exist.");
        return utilityRepository.save(newUtility);
    }

    public Utility getUtilityByType(String type) {
        Utility utility = utilityRepository.findByUtilityType(type);
        if (utility == null) throw new ResourceNotFoundException("Utility type '" + type + "' not found.");
        return utility;
    }

    public List<Utility> getAllUtilities() {
        return utilityRepository.findAll();
    }

    public List<Utility> getAvailableUtilities() {
        return utilityRepository.findByQuantityGreaterThan(0);
    }

    public Utility updateUtility(String type, Utility newUtility) {
        Utility utility = getUtilityByType(type);
        utility.setQuantity(newUtility.getQuantity());
        return utilityRepository.save(utility);
    }

    public void deleteUtility(String type) {
        Utility utility = getUtilityByType(type);
        utilityRepository.delete(utility);
    }
}
