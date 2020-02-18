package lk.apiit.eirlss.bangerandco.services;

import lk.apiit.eirlss.bangerandco.exceptions.CustomException;
import lk.apiit.eirlss.bangerandco.models.Utility;
import lk.apiit.eirlss.bangerandco.repositories.UtilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UtilityService {
    private UtilityRepository utilityRepository;

    @Autowired
    public UtilityService(UtilityRepository utilityRepository) {
        this.utilityRepository = utilityRepository;
    }

    public List<Utility> createUtility(Utility newUtility) {
        Utility utility = utilityRepository.findByUtilityType(newUtility.getUtilityType());
        if (utility != null) throw new CustomException("Utility type '" + newUtility.getUtilityType() + "' already exist.", HttpStatus.BAD_REQUEST);
        utilityRepository.save(newUtility);
        return getAllUtilities();
    }

    public Utility getUtilityByType(String type) {
        Utility utility = utilityRepository.findByUtilityType(type);
        if (utility == null) throw new CustomException("Utility type '" + type + "' not found.", HttpStatus.NOT_FOUND);
        return utility;
    }

    public List<Utility> getAllUtilities() {
        return utilityRepository.findAll();
    }

    public List<Utility> getAvailableUtilities() {
        return utilityRepository.findByQuantityGreaterThan(0);
    }

    public List<Utility> updateUtility(String type, Utility newUtility) {
        Utility utility = getUtilityByType(type);
        utility.setQuantity(newUtility.getQuantity());
        utilityRepository.save(utility);
        return getAllUtilities();
    }

    public void deleteUtility(String type) {
        Utility utility = getUtilityByType(type);
        utilityRepository.delete(utility);
    }
}
