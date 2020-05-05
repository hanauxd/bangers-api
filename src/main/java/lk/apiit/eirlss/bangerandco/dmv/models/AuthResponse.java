package lk.apiit.eirlss.bangerandco.dmv.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String username;
    private String expiryTime;
    private String jwt;
}
