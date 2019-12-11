package lk.apiit.eirlss.bangerandco.controllers;

import lk.apiit.eirlss.bangerandco.Convertors.UserConvertor;
import lk.apiit.eirlss.bangerandco.dto.UserDTO;
import lk.apiit.eirlss.bangerandco.models.User;
import lk.apiit.eirlss.bangerandco.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {
    private UserService service;
    private UserConvertor convertor;

    @Autowired
    public AuthenticationController(UserService service) {
        this.service = service;
        this.convertor = new UserConvertor();
    }

    @PostMapping("/login")
    public UserDTO login(@RequestBody UserDTO userDTO) {
        User user = service.getUserByEmail(userDTO.getEmail());
        boolean isValid = checkPassword(userDTO.getPassword(), user.getPassword());
        if (isValid) {
            System.out.println(userDTO.toString());
            return convertor.toUserDTO(user);
        }
        return new UserDTO();
    }

    private boolean checkPassword(String password, String hashPassword) {
        return BCrypt.checkpw(password, hashPassword);
    }
}
