package lk.apiit.eirlss.bangerandco.controllers;

import lk.apiit.eirlss.bangerandco.exceptions.CustomException;
import lk.apiit.eirlss.bangerandco.models.User;
import lk.apiit.eirlss.bangerandco.models.UserDocument;
import lk.apiit.eirlss.bangerandco.services.FileService;
import lk.apiit.eirlss.bangerandco.services.UserDocumentService;
import lk.apiit.eirlss.bangerandco.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/user-documents")
public class UserDocumentController {
    private UserDocumentService userDocumentService;
    private UserService userService;
    private FileService fileService;

    @Autowired
    public UserDocumentController(UserDocumentService userDocumentService, UserService userService, FileService fileService) {
        this.userDocumentService = userDocumentService;
        this.userService = userService;
        this.fileService = fileService;
    }

    @PreAuthorize("hasAnyRole('USER', 'STAFF', 'ADMIN')")
    @PostMapping("/upload")
    public ResponseEntity<?> createUserDocument(@RequestParam("file") MultipartFile uploadedFile, Authentication auth) {
        User user = userService.getUserByEmail(auth.getName());
        UserDocument persistedDocument = userDocumentService.createUserDocument(uploadedFile, user);
        return new ResponseEntity<>(persistedDocument, HttpStatus.CREATED);
    }

    @GetMapping("/download/{filename}")
    public ResponseEntity<?> downloadFile(@PathVariable String filename, HttpServletRequest request) {
        try {
            UrlResource resource = fileService.getResource(filename);
            String mimeType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
            return ResponseEntity
                    .ok()
                    .contentType(MediaType.parseMediaType(mimeType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename())
                    .body(resource);
        } catch (IOException e) {
            throw new CustomException("File not found.", HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasAnyRole('USER', 'STAFF', 'ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserDocument(@PathVariable String id) {
        String deletedFilename = userDocumentService.deleteUserDocument(id);
        return ResponseEntity.ok("User Document '" + deletedFilename + "' deleted.");
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
