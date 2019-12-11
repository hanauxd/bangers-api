package lk.apiit.eirlss.bangerandco.services;

import lk.apiit.eirlss.bangerandco.models.User;
import lk.apiit.eirlss.bangerandco.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private UserRepository repository;

    @Autowired
    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public void createUser(User user) {
        repository.save(user);
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

    public User getUserByEmail(String email) {
        return repository.findUserByEmail(email);
    }
}
