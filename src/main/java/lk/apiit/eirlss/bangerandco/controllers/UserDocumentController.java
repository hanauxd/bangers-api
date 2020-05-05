package lk.apiit.eirlss.bangerandco.controllers;

import lk.apiit.eirlss.bangerandco.models.User;
import lk.apiit.eirlss.bangerandco.models.UserDocument;
import lk.apiit.eirlss.bangerandco.services.FileService;
import lk.apiit.eirlss.bangerandco.services.UserDocumentService;
import lk.apiit.eirlss.bangerandco.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/user-documents")
public class UserDocumentController {
    private final UserDocumentService userDocumentService;
    private final UserService userService;
    private final FileService fileService;

    @Autowired
    public UserDocumentController(UserDocumentService userDocumentService, UserService userService, FileService fileService) {
        this.userDocumentService = userDocumentService;
        this.userService = userService;
        this.fileService = fileService;
    }

    @PreAuthorize("hasAnyRole('USER', 'STAFF', 'ADMIN')")
    @PostMapping("/upload")
    public ResponseEntity<?> createUserDocument(
            @RequestParam("file") MultipartFile uploadedFile,
            @RequestParam("dateIssued") Date dateIssued,
            @RequestParam("type") String type,
            Authentication auth
    ) {
        User user = userService.getUserByEmail(auth.getName());
        userDocumentService.createUserDocument(uploadedFile, dateIssued, type, user);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @GetMapping("/download/{filename}")
    public ResponseEntity<?> downloadFile(@PathVariable String filename, HttpServletRequest request) {
        return fileService.downloadFile(filename, request);
    }

    @PreAuthorize("hasAnyRole('USER', 'STAFF', 'ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserDocument(@PathVariable String id, Authentication auth) {
        userDocumentService.deleteUserDocument(id);
        User user = userService.getUserByEmail(auth.getName());
        List<UserDocument> documents = userDocumentService.getDocumentsByUser(user);
        return ResponseEntity.ok(documents);
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
