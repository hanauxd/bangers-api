package lk.apiit.eirlss.bangerandco.services;

import lk.apiit.eirlss.bangerandco.dto.responses.ComparisonResponse;
import lk.apiit.eirlss.bangerandco.dto.responses.FleetResponse;
import lk.apiit.eirlss.bangerandco.models.ExternalRate;
import lk.apiit.eirlss.bangerandco.models.Vehicle;
import lk.apiit.eirlss.bangerandco.repositories.ExternalRateRepository;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ExternalRateService {
    private final ExternalRateRepository externalRateRepository;
    private final VehicleService vehicleService;

    @Autowired
    public ExternalRateService(ExternalRateRepository externalRateRepository, VehicleService vehicleService) {
        this.externalRateRepository = externalRateRepository;
        this.vehicleService = vehicleService;
    }

    public List<ExternalRate> getByVehicle(String name) {
        return externalRateRepository.findAllByVehicle(name);
    }

    public void deleteAllInBatch() {
        externalRateRepository.deleteAllInBatch();
    }

    public void saveAllInBatch(List<ExternalRate> externalRates) {
        externalRateRepository.saveAll(externalRates);
    }

    public FleetResponse getFleetComparison() {
        List<ComparisonResponse> comparisons = new ArrayList<>();
        List<ExternalRate> externalFleet = externalRateRepository.findAll();
        externalFleet.forEach(externalRate -> {
            Vehicle ourId = vehicleService.getVehicleById(externalRate.getOurVehicleId());
            comparisons.add(new ComparisonResponse(externalRate.getVehicle(), externalRate.getRate(), ourId.getPrice()));
        });
        return new FleetResponse(comparisons);
    }

    public double average(String name) {
        List<ExternalRate> externalRates = getByVehicle(name);
        double rate = 0;
        for (ExternalRate extRate : externalRates) {
            rate += extRate.getRate();
        }
        return rate / externalRates.size();
    }

    public void persistExternalRates(Elements externalFleet, Elements rates) {
        deleteAllInBatch();

        List<ExternalRate> matches = new ArrayList<>();
        List<Vehicle> ourFleet = vehicleService.getAllVehicles();

        int i = 0;
        for (Element element : externalFleet) {
            for (Vehicle vehicle : ourFleet) {
                String name = vehicle.getBrand().concat(" ").concat(vehicle.getModel());
                if (element.text().contains(name)) {
                    matches.add(new ExternalRate(name, rate(rates.get(i)), vehicle.getId()));
                }
            }
            i += 2;
        }
        saveAllInBatch(matches);
    }

    private double rate(Element rate) {
        return Double.parseDouble(rate.text().replace(",", ""));
    }
}
