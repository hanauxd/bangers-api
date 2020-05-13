package lk.apiit.eirlss.bangerandco.dto.responses;

import lk.apiit.eirlss.bangerandco.models.ExternalRate;
import lk.apiit.eirlss.bangerandco.models.Vehicle;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FleetResponse {
    private List<Vehicle> ourFleet;
    private List<ExternalRate> externalFleet;
}
