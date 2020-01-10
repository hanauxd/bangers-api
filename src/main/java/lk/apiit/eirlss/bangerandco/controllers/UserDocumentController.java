package lk.apiit.eirlss.bangerandco.controllers;

import lk.apiit.eirlss.bangerandco.dto.requests.UserDocumentDTO;
import lk.apiit.eirlss.bangerandco.models.User;
import lk.apiit.eirlss.bangerandco.models.UserDocument;
import lk.apiit.eirlss.bangerandco.services.UserDocumentService;
import lk.apiit.eirlss.bangerandco.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user-documents")
public class UserDocumentController {
    private UserDocumentService userDocumentService;
    private UserService userService;

    @Autowired
    public UserDocumentController(UserDocumentService userDocumentService, UserService userService) {
        this.userDocumentService = userDocumentService;
        this.userService = userService;
    }

    @PreAuthorize("hasAnyRole('USER', 'STAFF', 'ADMIN')")
    @PostMapping
    public ResponseEntity<?> createUserDocument(@RequestBody UserDocumentDTO documentDTO, Authentication auth) {
        User user = userService.getUserByEmail(auth.getName());
        UserDocument userDocument = documentDTO.transformToEntity();
        userDocument.setUser(user);
        user.getDocuments().add(userDocument);
        UserDocument persistedDocument = userDocumentService.createUserDocument(userDocument);
        return new ResponseEntity<>(persistedDocument, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('USER', 'STAFF', 'ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserDocument(@PathVariable String id) {
        userDocumentService.deleteUserDocument(id);
        return ResponseEntity.ok("User Document deleted.");
    }

    @PreAuthorize("hasAnyRole('USER', 'STAFF', 'ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserDocumentById(@PathVariable String id) {
        UserDocument document = userDocumentService.getUserDocumentById(id);
        return ResponseEntity.ok(document);
    }

    @PreAuthorize("hasAnyRole('USER', 'STAFF', 'ADMIN')")
    @GetMapping("/user/{id}")
    public ResponseEntity<?> getUserDocumentsByUser(@PathVariable String id) {
        User user = userService.getUserById(id);
        List<UserDocument> documents = userDocumentService.getDocumentsByUser(user);
        return ResponseEntity.ok(documents);
    }
}
