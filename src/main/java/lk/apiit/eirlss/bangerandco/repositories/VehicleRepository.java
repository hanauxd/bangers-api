package lk.apiit.eirlss.bangerandco.repositories;

import lk.apiit.eirlss.bangerandco.models.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, String> {
    Optional<Vehicle> findByLicense(String license);
}
