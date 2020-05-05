package lk.apiit.eirlss.bangerandco.services;

import lk.apiit.eirlss.bangerandco.exceptions.CustomException;
import lk.apiit.eirlss.bangerandco.models.Vehicle;
import lk.apiit.eirlss.bangerandco.models.VehicleImage;
import lk.apiit.eirlss.bangerandco.repositories.VehicleImageRepository;
import lk.apiit.eirlss.bangerandco.repositories.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class VehicleService {
    private final VehicleRepository vehicleRepository;
    private final VehicleImageRepository vehicleImageRepository;
    private final FileService fileService;

    @Autowired
    public VehicleService(VehicleRepository vehicleRepository, VehicleImageRepository vehicleImageRepository, FileService fileService) {
        this.vehicleRepository = vehicleRepository;
        this.vehicleImageRepository = vehicleImageRepository;
        this.fileService = fileService;
    }

    public List<Vehicle> createVehicle(Vehicle vehicle, MultipartFile[] files) {
        boolean isExist = vehicleRepository.findByLicense(vehicle.getLicense()).isPresent();
        if (isExist) throw new CustomException("License number already exist.", HttpStatus.BAD_REQUEST);
        Vehicle persistedVehicle = vehicleRepository.save(vehicle);
        createVehicleImage(persistedVehicle, files);
        return getAllVehicles();
    }

    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    public Vehicle getVehicleById(String id) {
        Vehicle vehicle = vehicleRepository.findById(id).orElse(null);
        if (vehicle == null) throw new CustomException("Vehicle not found.", HttpStatus.NOT_FOUND);
        return vehicle;
    }

    public Vehicle updateVehicle(String id, Vehicle vehicle) {
        Vehicle persistedVehicle = getVehicleById(id);
        persistedVehicle.setDescription(vehicle.getDescription());
        persistedVehicle.setBrand(vehicle.getBrand());
        persistedVehicle.setCategory(vehicle.getCategory());
        persistedVehicle.setModel(vehicle.getModel());
        persistedVehicle.setLicense(vehicle.getLicense());
        persistedVehicle.setFuelType(vehicle.getFuelType());
        persistedVehicle.setTransmissionType(vehicle.getTransmissionType());
        persistedVehicle.setPrice(vehicle.getPrice());
        persistedVehicle.setSize(vehicle.getSize());
        return vehicleRepository.save(persistedVehicle);
    }

    public void deleteVehicle(String id) {
        Vehicle vehicle = getVehicleById(id);
        vehicleRepository.delete(vehicle);
    }

    private void createVehicleImage(Vehicle vehicle, MultipartFile[] files) {
        for (MultipartFile file : files) {
            String filename = fileService.store(file);
            VehicleImage vehicleImage = new VehicleImage(vehicle, filename);
            vehicleImageRepository.save(vehicleImage);
        }
    }
}
