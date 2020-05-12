package lk.apiit.eirlss.bangerandco.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DMVAuthResponse {
    private String username;
    private String expiryTime;
    private String jwt;
}
