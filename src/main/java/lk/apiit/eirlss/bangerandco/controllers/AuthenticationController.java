package lk.apiit.eirlss.bangerandco.controllers;

import lk.apiit.eirlss.bangerandco.dto.requests.AuthenticationRequest;
import lk.apiit.eirlss.bangerandco.dto.requests.UserDTO;
import lk.apiit.eirlss.bangerandco.dto.responses.AuthenticationResponse;
import lk.apiit.eirlss.bangerandco.exceptions.CustomException;
import lk.apiit.eirlss.bangerandco.models.User;
import lk.apiit.eirlss.bangerandco.models.UserDetailsImpl;
import lk.apiit.eirlss.bangerandco.security.JwtUtil;
import lk.apiit.eirlss.bangerandco.services.MapValidationErrorService;
import lk.apiit.eirlss.bangerandco.services.UserDetailsServiceImpl;
import lk.apiit.eirlss.bangerandco.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*")
@RestController
public class AuthenticationController {
    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userServiceDetails;
    private final JwtUtil jwtUtil;
    private final MapValidationErrorService mapValidationErrorService;
    private final ModelMapper modelMapper;
    private final UserService userService;

    @Value("${app.token.expiry-time}")
    private String expiryTime;

    @Autowired
    public AuthenticationController(UserDetailsServiceImpl userServiceDetails,
                                    AuthenticationManager authenticationManager,
                                    JwtUtil jwtUtil,
                                    MapValidationErrorService mapValidationErrorService,
                                    UserService userService) {
        this.userServiceDetails = userServiceDetails;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.mapValidationErrorService = mapValidationErrorService;
        this.userService = userService;
        this.modelMapper = new ModelMapper();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest authRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
            UserDetailsImpl userDetails = (UserDetailsImpl) userServiceDetails.loadUserByUsername(authRequest.getUsername());
            String jwt = jwtUtil.generateToken(userDetails);
            String userRole = userDetails.getUserRole();
            String userId = userDetails.getUserId();
            return ResponseEntity.ok(new AuthenticationResponse(userId, userDetails.getUsername(), expiryTime, jwt, userRole));
        } catch (BadCredentialsException e) {
            throw new CustomException("Invalid username or password", HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserDTO dto, BindingResult result) {
        if (result.hasErrors()) return mapValidationErrorService.mapValidationErrorService(result);
        User user = modelMapper.map(dto, User.class);
        User savedUser = userService.createUser(user);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }
}
