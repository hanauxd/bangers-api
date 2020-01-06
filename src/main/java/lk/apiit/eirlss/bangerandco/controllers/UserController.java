package lk.apiit.eirlss.bangerandco.controllers;

import lk.apiit.eirlss.bangerandco.dto.UserDTO;
import lk.apiit.eirlss.bangerandco.models.User;
import lk.apiit.eirlss.bangerandco.services.MapValidationErrorService;
import lk.apiit.eirlss.bangerandco.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> userList = service.getAllUsers();
        List<UserDTO> dtoList = Arrays.asList(modelMapper.map(userList, UserDTO[].class));
        return new ResponseEntity<>(dtoList, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('USER', 'STAFF', 'ADMIN')")
    @GetMapping("/{id}")
    public UserDTO getUserById(@PathVariable String id) {
        User user = service.getUserById(id);
        return modelMapper.map(user, UserDTO.class);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@RequestBody UserDTO userDTO, @PathVariable String id, BindingResult result) {
        if (result.hasErrors()) return mapValidationErrorService.mapValidationErrorService(result);
        User user = service.getUserById(id);
        modelMapper.map(userDTO, user);
        User newUser = service.updateUser(user);
        UserDTO dto = modelMapper.map(newUser, UserDTO.class);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        service.deleteUser(id);
        return new ResponseEntity<>("User is deleted.", HttpStatus.OK);
    }
}
