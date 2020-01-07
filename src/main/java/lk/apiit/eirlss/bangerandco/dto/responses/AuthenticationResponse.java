package lk.apiit.eirlss.bangerandco.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthenticationResponse {
    private final String username;
    private final String expiryTime;
    private final String jwt;
}
