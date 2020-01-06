package lk.apiit.eirlss.bangerandco.controllers;

import lk.apiit.eirlss.bangerandco.models.Vehicle;
import lk.apiit.eirlss.bangerandco.services.MapValidationErrorService;
import lk.apiit.eirlss.bangerandco.services.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/vehicles")
public class VehicleController {

    private VehicleService vehicleService;
    private MapValidationErrorService mapValidationErrorService;

    @Autowired
    public VehicleController(VehicleService vehicleService, MapValidationErrorService mapValidationErrorService) {
        this.vehicleService = vehicleService;
        this.mapValidationErrorService = mapValidationErrorService;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @PostMapping
    public ResponseEntity<?> createVehicle(@Valid @RequestBody Vehicle vehicle, BindingResult result) {
        if (result.hasErrors()) return mapValidationErrorService.mapValidationErrorService(result);
        Vehicle persistedVehicle = vehicleService.createVehicle(vehicle);
        return new ResponseEntity<>(persistedVehicle, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    @GetMapping
    public ResponseEntity<?> getAllVehicles() {
        List<Vehicle> vehicles = vehicleService.getAllVehicles();
        return new ResponseEntity<>(vehicles, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    @GetMapping("/{license}")
    public ResponseEntity<?> getVehicleByLicense(@PathVariable String license) {
        Vehicle vehicle = vehicleService.getVehicleByLicense(license);
        return new ResponseEntity<>(vehicle, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @PutMapping("/{license}")
    public ResponseEntity<?> updateVehicle(@Valid @PathVariable String license, @RequestBody Vehicle vehicle, BindingResult result) {
        if (result.hasErrors()) mapValidationErrorService.mapValidationErrorService(result);
        Vehicle updatedVehicle = vehicleService.updateVehicle(license, vehicle);
        return new ResponseEntity<>(updatedVehicle, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @DeleteMapping("/{license}")
    public ResponseEntity<?> deleteVehicle(@PathVariable String license) {
        vehicleService.deleteVehicle(license);
        return new ResponseEntity<>("Vehicle is deleted.", HttpStatus.OK);
    }
}
