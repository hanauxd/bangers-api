package lk.apiit.eirlss.bangerandco.controllers;

import lk.apiit.eirlss.bangerandco.services.ExternalRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/external-rates")
public class ExternalRateController {
    private final ExternalRateService externalRateService;

    @Autowired
    public ExternalRateController(ExternalRateService externalRateService) {
        this.externalRateService = externalRateService;
    }

    @GetMapping("/{name}")
    public ResponseEntity<?> getAverageRateForVehicle(@PathVariable String name) {
        return ResponseEntity.ok(externalRateService.average(name));
    }

    @GetMapping("/our-rates")
    public ResponseEntity<?> getOurRates() {
        return ResponseEntity.ok(externalRateService.getFleetComparison());
    }
}
