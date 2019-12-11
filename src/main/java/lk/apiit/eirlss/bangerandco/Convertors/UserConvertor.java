package lk.apiit.eirlss.bangerandco.Convertors;

import lk.apiit.eirlss.bangerandco.dto.UserDTO;
import lk.apiit.eirlss.bangerandco.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserConvertor {
    public User toUserModel(UserDTO dto) {
        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setRole(dto.getRole());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setDateOfBirth(dto.getDob());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        return user;
    }

    public UserDTO toUserDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setRole(user.getRole());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setDob(user.getDateOfBirth());
        dto.setPhone(user.getPhone());
        dto.setEmail(user.getEmail());
        dto.setPassword(user.getPassword());
        return dto;
    }

    public List<UserDTO> toUserDTOList(List<User> userList) {
        List<UserDTO> userDTOList = new ArrayList<>();
        userList.forEach(user -> userDTOList.add(toUserDTO(user)));
        return userDTOList;
    }
}
