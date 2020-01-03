package lk.apiit.eirlss.bangerandco.services;

import lk.apiit.eirlss.bangerandco.exceptions.EmailAlreadyExistException;
import lk.apiit.eirlss.bangerandco.models.User;
import lk.apiit.eirlss.bangerandco.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private UserRepository repository;

    @Autowired
    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public User createUser(User user) {
        try {
            return repository.save(user);
        } catch (Exception e) {
            throw new EmailAlreadyExistException("Email " + user.getEmail() + " already exist.");
        }
    }

    public List<User> getAllUsers() {
        return repository.findAll();
    }

    public User getUserById(String id) {
        return repository.findById(id).orElse(null);
    }

    public User updateUser(User user) {
        return repository.save(user);
    }

    public Optional<User> getUserByEmail(String email) {
        return repository.findUserByEmail(email);
    }
}
