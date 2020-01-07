package lk.apiit.eirlss.bangerandco.controllers;

import lk.apiit.eirlss.bangerandco.models.Utility;
import lk.apiit.eirlss.bangerandco.services.MapValidationErrorService;
import lk.apiit.eirlss.bangerandco.services.UtilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/utilities")
public class UtilityController {
    private UtilityService utilityService;
    private MapValidationErrorService mapValidationErrorService;

    @Autowired
    public UtilityController(UtilityService utilityService, MapValidationErrorService mapValidationErrorService) {
        this.utilityService = utilityService;
        this.mapValidationErrorService = mapValidationErrorService;
    }

    @PostMapping
    public ResponseEntity<?> createUtility(@RequestBody Utility newUtility, BindingResult result) {
        if (result.hasErrors()) return mapValidationErrorService.mapValidationErrorService(result);
        Utility utility = utilityService.createUtility(newUtility);
        return new ResponseEntity<>(utility, HttpStatus.CREATED);
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

    @PutMapping("/{type}")
    public ResponseEntity<?> updateUtility(@PathVariable String type, @RequestBody Utility utility, BindingResult result) {
        if (result.hasErrors()) mapValidationErrorService.mapValidationErrorService(result);
        Utility updatedUtility = utilityService.updateUtility(type, utility);
        return new ResponseEntity<>(updatedUtility, HttpStatus.OK);
    }

    @DeleteMapping("/{type}")
    public ResponseEntity<?> deleteUtility(@PathVariable String type) {
        utilityService.deleteUtility(type);
        return new ResponseEntity<>("Utility type '" + type + "' deleted.", HttpStatus.OK);
    }
}
