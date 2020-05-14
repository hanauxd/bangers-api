package lk.apiit.eirlss.bangerandco.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FleetResponse {
    private List<ComparisonResponse> comparisonResponses;
}
