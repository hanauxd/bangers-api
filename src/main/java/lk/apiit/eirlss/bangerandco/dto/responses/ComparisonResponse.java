package lk.apiit.eirlss.bangerandco.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComparisonResponse {
    private String vehicle;
    private double externalRate;
    private double ourRate;
}
