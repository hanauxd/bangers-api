package lk.apiit.eirlss.bangerandco.controllers;

import lk.apiit.eirlss.bangerandco.dto.requests.UtilityRequest;
import lk.apiit.eirlss.bangerandco.models.Utility;
import lk.apiit.eirlss.bangerandco.services.MapValidationErrorService;
import lk.apiit.eirlss.bangerandco.services.UtilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/utilities")
public class UtilityController {
    private final UtilityService utilityService;
    private final MapValidationErrorService mapValidationErrorService;

    @Autowired
    public UtilityController(UtilityService utilityService, MapValidationErrorService mapValidationErrorService) {
        this.utilityService = utilityService;
        this.mapValidationErrorService = mapValidationErrorService;
    }

    @PreAuthorize("hasAnyRole('USER', 'STAFF', 'ADMIN')")
    @PostMapping
    public ResponseEntity<?> createUtility(@RequestBody UtilityRequest dto, BindingResult result) {
        if (result.hasErrors()) return mapValidationErrorService.mapValidationErrorService(result);
        List<Utility> utilities = utilityService.createUtility(dto.transformToEntity());
        return new ResponseEntity<>(utilities, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<?> getAllUtilities() {
        List<Utility> utilities = utilityService.getAllUtilities();
        return new ResponseEntity<>(utilities, HttpStatus.OK);
    }

    @GetMapping("/available")
    public ResponseEntity<?> getAvailableUtilities() {
        List<Utility> utilities = utilityService.getAvailableUtilities();
        return new ResponseEntity<>(utilities, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('USER', 'STAFF', 'ADMIN')")
    @PutMapping("/{type}")
    public ResponseEntity<?> updateUtility(@PathVariable String type, @RequestBody Utility utility, BindingResult result) {
        if (result.hasErrors()) mapValidationErrorService.mapValidationErrorService(result);
        List<Utility> utilities = utilityService.updateUtility(type, utility);
        return new ResponseEntity<>(utilities, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('USER', 'STAFF', 'ADMIN')")
    @DeleteMapping("/{type}")
    public ResponseEntity<?> deleteUtility(@PathVariable String type) {
        utilityService.deleteUtility(type);
        return new ResponseEntity<>("Utility type '" + type + "' deleted.", HttpStatus.OK);
    }
}
