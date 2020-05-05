package lk.apiit.eirlss.bangerandco.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lk.apiit.eirlss.bangerandco.models.Vehicle;
import lk.apiit.eirlss.bangerandco.services.FileService;
import lk.apiit.eirlss.bangerandco.services.MapValidationErrorService;
import lk.apiit.eirlss.bangerandco.services.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/vehicles")
public class VehicleController {
    private final FileService fileService;
    private final VehicleService vehicleService;
    private final MapValidationErrorService mapValidationErrorService;

    @Autowired
    public VehicleController(FileService fileService, VehicleService vehicleService, MapValidationErrorService mapValidationErrorService) {
        this.fileService = fileService;
        this.vehicleService = vehicleService;
        this.mapValidationErrorService = mapValidationErrorService;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @PostMapping
    public ResponseEntity<?> createVehicle(@Valid @RequestParam("vehicle") String vehicleDTO, @RequestParam("file") MultipartFile[] files) throws JsonProcessingException {
        Vehicle vehicle = new ObjectMapper().readValue(vehicleDTO, Vehicle.class);
        List<Vehicle> vehicles = vehicleService.createVehicle(vehicle, files);
        return new ResponseEntity<>(vehicles, HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllVehicles() {
        List<Vehicle> vehicles = vehicleService.getAllVehicles();
        return new ResponseEntity<>(vehicles, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getVehicleById(@PathVariable String id) {
        Vehicle vehicle = vehicleService.getVehicleById(id);
        return new ResponseEntity<>(vehicle, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateVehicle(@Valid @PathVariable String id, @RequestBody Vehicle vehicle, BindingResult result) {
        if (result.hasErrors()) mapValidationErrorService.mapValidationErrorService(result);
        Vehicle updatedVehicle = vehicleService.updateVehicle(id, vehicle);
        return new ResponseEntity<>(updatedVehicle, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVehicle(@PathVariable String id) {
        vehicleService.deleteVehicle(id);
        return new ResponseEntity<>("Vehicle is deleted.", HttpStatus.OK);
    }

    @GetMapping("/images/download/{filename}")
    public ResponseEntity<?> downloadVehicleImage(@PathVariable String filename, HttpServletRequest request) {
        return fileService.downloadFile(filename, request);
    }
}