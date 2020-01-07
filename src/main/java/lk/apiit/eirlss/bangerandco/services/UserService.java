package lk.apiit.eirlss.bangerandco.services;

import lk.apiit.eirlss.bangerandco.exceptions.CustomException;
import lk.apiit.eirlss.bangerandco.models.User;
import lk.apiit.eirlss.bangerandco.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private UserRepository repository;

    @Autowired
    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public User createUser(User user) {
        boolean isExist = repository.findUserByEmail(user.getEmail()).isPresent();
        if (isExist) throw new CustomException("Email '" + user.getEmail() + "' already exist.", HttpStatus.BAD_REQUEST);
        user.setPassword(hashPassword(user.getPassword()));
        return repository.save(user);
    }

    public List<User> getAllUsers() {
        return repository.findAll();
    }

    public User getUserById(String id) {
        User user = repository.findById(id).orElse(null);
        if (user == null) throw new CustomException("User not found.", HttpStatus.NOT_FOUND);
        return user;
    }

    public User updateUser(User user) {
        user.setPassword(hashPassword(user.getPassword()));
        return repository.save(user);
    }

    public User getUserByEmail(String email) {
        User user = repository.findUserByEmail(email).orElse(null);
        if (user == null) throw new UsernameNotFoundException("Email '" + email + "' not found.");
        return user;
    }

    public void deleteUser(String id) {
        User user = getUserById(id);
        repository.delete(user);
    }

    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
}
