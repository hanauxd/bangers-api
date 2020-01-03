package lk.apiit.eirlss.bangerandco.models;

import lombok.Data;

@Data
public class AuthenticationRequest {
    private String username;
    private String password;
}
