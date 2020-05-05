package lk.apiit.eirlss.bangerandco.services;

import lk.apiit.eirlss.bangerandco.exceptions.CustomException;
import lk.apiit.eirlss.bangerandco.models.User;
import lk.apiit.eirlss.bangerandco.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class UserService {
    private final UserRepository repository;
    private final FileService fileService;

    @Autowired
    public UserService(UserRepository repository, FileService fileService) {
        this.repository = repository;
        this.fileService = fileService;
    }

    public User createUser(User user) {
        boolean isExist = repository.findUserByEmail(user.getEmail()).isPresent();
        if (isExist)
            throw new CustomException("Email '" + user.getEmail() + "' already exist.", HttpStatus.BAD_REQUEST);
        user.setPassword(hashPassword(user.getPassword()));
        return repository.save(user);
    }

    public List<User> getAllCustomers() {
        return repository.findUsersByRole("ROLE_USER");
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

    public List<User> blacklistUser(String id, boolean isBlacklisted) {
        User user = getUserById(id);
        user.setBlacklisted(isBlacklisted);
        repository.save(user);
        return getBlacklistedUsers();
    }

    public List<User> getBlacklistedUsers() {
        return repository.findUsersByBlacklisted(true);
    }

    public void profileImage(MultipartFile file, User user) {
        String profileImage = user.getProfileImage();
        if (profileImage != null) {
            try {
                Path path = fileService.getPath(profileImage);
                Files.deleteIfExists(path);
            } catch (IOException e) {
                throw new CustomException("Failed to delete the file.", HttpStatus.BAD_REQUEST);
            }
        }
        String filename = fileService.store(file);
        user.setProfileImage(filename);
        repository.save(user);
    }

    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
}
