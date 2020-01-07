package lk.apiit.eirlss.bangerandco.services;

import lk.apiit.eirlss.bangerandco.exceptions.CustomException;
import lk.apiit.eirlss.bangerandco.models.Vehicle;
import lk.apiit.eirlss.bangerandco.repositories.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehicleService {
    private final VehicleRepository vehicleRepository;

    @Autowired
    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public Vehicle createVehicle(Vehicle vehicle) {
        boolean isExist = vehicleRepository.findByLicense(vehicle.getLicense()).isPresent();
        if (isExist) throw new CustomException("License number already exist.", HttpStatus.BAD_REQUEST);
        return vehicleRepository.save(vehicle);
    }

    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    public Vehicle getVehicleByLicense(String license) {
        Vehicle vehicle = vehicleRepository.findByLicense(license).orElse(null);
        if (vehicle == null) throw new CustomException("Vehicle not found.", HttpStatus.NOT_FOUND);
        return vehicle;
    }

    public Vehicle updateVehicle(String license, Vehicle vehicle) {
        Vehicle persistedVehicle = getVehicleByLicense(license);
        persistedVehicle.setDescription(vehicle.getDescription());
        persistedVehicle.setBrand(vehicle.getBrand());
        persistedVehicle.setCategory(vehicle.getCategory());
        persistedVehicle.setModel(vehicle.getModel());
        persistedVehicle.setLicense(vehicle.getLicense());
        return vehicleRepository.save(persistedVehicle);
    }

    public void deleteVehicle(String license) {
        Vehicle vehicle = getVehicleByLicense(license);
        vehicleRepository.delete(vehicle);
    }
}
