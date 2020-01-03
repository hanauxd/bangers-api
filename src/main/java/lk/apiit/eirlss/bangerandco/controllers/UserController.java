package lk.apiit.eirlss.bangerandco.controllers;

import lk.apiit.eirlss.bangerandco.dto.UserDTO;
import lk.apiit.eirlss.bangerandco.models.User;
import lk.apiit.eirlss.bangerandco.services.MapValidationErrorService;
import lk.apiit.eirlss.bangerandco.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private UserService service;
    private ModelMapper modelMapper;
    private MapValidationErrorService mapValidationErrorService;

    @Autowired
    public UserController(UserService service, MapValidationErrorService mapValidationErrorService) {
        this.service = service;
        this.modelMapper = new ModelMapper();
        this.mapValidationErrorService = mapValidationErrorService;
    }

    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody UserDTO userDTO, BindingResult result) {

        if (result.hasErrors()) return mapValidationErrorService.mapValidationErrorService(result);

        String hashedPassword = hashPassword(userDTO.getPassword());
        userDTO.setPassword(hashedPassword);
        User user = modelMapper.map(userDTO, User.class);
        User savedUser = service.createUser(user);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @GetMapping
    public List<UserDTO> getAllUsers() {
        List<User> userList = service.getAllUsers();
        return Arrays.asList(modelMapper.map(userList, UserDTO[].class));
    }

    @GetMapping("/{id}")
    public UserDTO getUserById(@PathVariable String id) {
        User user = service.getUserById(id);
        return modelMapper.map(user, UserDTO.class);
    }

    @PutMapping("/{id}")
    public UserDTO updateUser(@RequestBody UserDTO userDTO, @PathVariable String id) {
        User user = service.getUserById(id);
        String hashPassword = hashPassword(userDTO.getPassword());
        userDTO.setPassword(hashPassword);
        modelMapper.map(userDTO, user);
        User newUser = service.updateUser(user);
        return modelMapper.map(newUser, UserDTO.class);
    }

    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
}
