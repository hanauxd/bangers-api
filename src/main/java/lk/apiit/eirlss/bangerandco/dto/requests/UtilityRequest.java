package lk.apiit.eirlss.bangerandco.dto.requests;

import lk.apiit.eirlss.bangerandco.models.Utility;
import lombok.Data;

@Data
public class UtilityRequest {
    private String utilityType;
    private int quantity;

    public Utility transformToEntity() {
        Utility utility = new Utility();
        utility.setUtilityType(this.utilityType);
        utility.setQuantity(this.quantity);
        return utility;
    }
}
