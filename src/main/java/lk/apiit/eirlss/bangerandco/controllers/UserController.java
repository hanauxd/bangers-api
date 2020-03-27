package lk.apiit.eirlss.bangerandco.controllers;

import lk.apiit.eirlss.bangerandco.dto.requests.UserDTO;
import lk.apiit.eirlss.bangerandco.models.User;
import lk.apiit.eirlss.bangerandco.services.MapValidationErrorService;
import lk.apiit.eirlss.bangerandco.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/users")
public class UserController {
    private UserService userService;
    private ModelMapper modelMapper;
    private MapValidationErrorService mapValidationErrorService;

    @Autowired
    public UserController(UserService userService, MapValidationErrorService mapValidationErrorService) {
        this.userService = userService;
        this.modelMapper = new ModelMapper();
        this.mapValidationErrorService = mapValidationErrorService;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllCustomers() {
        List<User> userList = userService.getAllCustomers();
        List<UserDTO> dtoList = Arrays.asList(modelMapper.map(userList, UserDTO[].class));
        return new ResponseEntity<>(dtoList, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('USER', 'STAFF', 'ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable String id) {
        User user = userService.getUserById(id);
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        return ResponseEntity.ok(userDTO);
    }

    @PreAuthorize("hasAnyRole('USER', 'STAFF', 'ADMIN')")
    @GetMapping("/auth-user")
    public ResponseEntity<?> getAuthUser(Authentication auth) {
        User user = userService.getUserByEmail(auth.getName());
        return ResponseEntity.ok(user);
    }

    @PreAuthorize("hasAnyRole('USER', 'STAFF', 'ADMIN')")
    @PostMapping("/username")
    public ResponseEntity<?> getUserByEmail(@RequestParam String email) {
        User user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@RequestBody UserDTO userDTO, @PathVariable String id, BindingResult result) {
        if (result.hasErrors()) return mapValidationErrorService.mapValidationErrorService(result);
        User user = userService.getUserById(id);
        modelMapper.map(userDTO, user);
        User newUser = userService.updateUser(user);
        UserDTO dto = modelMapper.map(newUser, UserDTO.class);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return new ResponseEntity<>("User is deleted.", HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @PostMapping("/blacklist/{id}")
    public ResponseEntity<?> blacklistUser(@PathVariable String id) {
        List<User> blacklistUser = userService.blacklistUser(id, false);
        return new ResponseEntity<>(blacklistUser, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @GetMapping("/blacklisted-users")
    public ResponseEntity<?> getBlacklistedUsers() {
        List<User> blacklistedUsers = userService.getBlacklistedUsers();
        return ResponseEntity.ok(blacklistedUsers);
    }
}
