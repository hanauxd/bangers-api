package lk.apiit.eirlss.bangerandco.repositories;

import lk.apiit.eirlss.bangerandco.models.Utility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UtilityRepository extends JpaRepository<Utility, String> {
    Utility findByUtilityType(String type);
    List<Utility> findByQuantityGreaterThan(int qty);
}
