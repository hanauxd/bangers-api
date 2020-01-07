package lk.apiit.eirlss.bangerandco.dto.requests;

import lombok.Data;

@Data
public class AuthenticationRequest {
    private String username;
    private String password;
}
