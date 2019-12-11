package lk.apiit.eirlss.bangerandco.controllers;

import lk.apiit.eirlss.bangerandco.Convertors.UserConvertor;
import lk.apiit.eirlss.bangerandco.dto.UserDTO;
import lk.apiit.eirlss.bangerandco.models.User;
import lk.apiit.eirlss.bangerandco.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private UserService service;
    private UserConvertor convertor;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
        this.convertor = new UserConvertor();
    }

    @PostMapping
    public void createUser(@RequestBody UserDTO userDTO) {
        String hashedPassword = hashPassword(userDTO.getPassword());
        userDTO.setPassword(hashedPassword);
        User user = convertor.toUserModel(userDTO);
        service.createUser(user);
    }

    @GetMapping
    public List<UserDTO> getAllUsers() {
        List<User> userList = service.getAllUsers();
        return convertor.toUserDTOList(userList);
    }

    @GetMapping("/{id}")
    public UserDTO getUserById(@PathVariable String id) {
        User user = service.getUserById(id);
        return convertor.toUserDTO(user);
    }

    @PutMapping("/{id}")
    public UserDTO updateUser(@RequestBody UserDTO userDTO, @PathVariable String id) {
        User user = service.getUserById(id);
        User updatedUser = updateUser(user, userDTO);
        User newUser = service.updateUser(updatedUser);
        return convertor.toUserDTO(newUser);
    }

    @PostMapping("/login")
    public UserDTO auth(@RequestBody UserDTO userDTO) {
        User user = service.getUserByEmail(userDTO.getEmail());
        boolean isValid = checkPassword(userDTO.getPassword(), user.getPassword());
        if (isValid) {
            return convertor.toUserDTO(user);
        }
        return new UserDTO();
    }

    private User updateUser(User user, UserDTO dto) {
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setDateOfBirth(dto.getDob());
        user.setPassword(dto.getPassword());
        user.setRole(dto.getRole());
        user.setPhone(dto.getPhone());
        return user;
    }

    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    private boolean checkPassword(String password, String hashPassword) {
        return BCrypt.checkpw(password, hashPassword);
    }
}
