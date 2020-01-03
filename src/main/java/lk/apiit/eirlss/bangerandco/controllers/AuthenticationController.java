package lk.apiit.eirlss.bangerandco.controllers;

import lk.apiit.eirlss.bangerandco.components.JwtUtil;
import lk.apiit.eirlss.bangerandco.models.AuthenticationRequest;
import lk.apiit.eirlss.bangerandco.models.AuthenticationResponse;
import lk.apiit.eirlss.bangerandco.services.UserDetailsServiceImpl;
import lk.apiit.eirlss.bangerandco.services.UserService;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {
    private UserService service;
    private ModelMapper modelMapper;
    private UserDetailsServiceImpl userServiceDetails;
    private final AuthenticationManager authenticationManager;
    private JwtUtil jwtUtil;

    @Autowired
    public AuthenticationController(UserService service, UserDetailsServiceImpl userServiceDetails, AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.service = service;
        this.userServiceDetails = userServiceDetails;
        this.jwtUtil = jwtUtil;
        this.modelMapper = new ModelMapper();
        this.modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest authRequest) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
            UserDetails userDetails = userServiceDetails.loadUserByUsername(authRequest.getUsername());
            String jwt = jwtUtil.generateToken(userDetails);
            return ResponseEntity.ok(new AuthenticationResponse(jwt));
        } catch (BadCredentialsException e) {
            throw new Exception("Invalid username or password", e);
        }
    }
}
