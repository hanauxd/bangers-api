package lk.apiit.eirlss.bangerandco.services;

import lk.apiit.eirlss.bangerandco.exceptions.CustomException;
import lk.apiit.eirlss.bangerandco.models.User;
import lk.apiit.eirlss.bangerandco.models.UserDocument;
import lk.apiit.eirlss.bangerandco.repositories.UserDocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;

@Service
public class UserDocumentService {
    private final UserDocumentRepository documentRepository;
    private final FileService fileService;

    @Autowired
    public UserDocumentService(UserDocumentRepository documentRepository, FileService fileService) {
        this.documentRepository = documentRepository;
        this.fileService = fileService;
    }

    public UserDocument getUserDocumentById(String id) {
        return documentRepository.findById(id).orElseThrow(() -> new CustomException("Document not found.", HttpStatus.NOT_FOUND));
    }

    public UserDocument createUserDocument(MultipartFile file, Date dateIssued, String type, User user) {
        deleteIfTypeExists(type, user);
        String filename = fileService.store(file);
        UserDocument document = new UserDocument(filename, type, dateIssued, user);
        user.getDocuments().add(document);
        return documentRepository.save(document);
    }

    public void deleteUserDocument(String id) {
        try {
            UserDocument document = getUserDocumentById(id);
            String filename = document.getFilename();
            Path path = fileService.getPath(filename);
            Files.deleteIfExists(path);
            document.getUser().removeUserDocument(document);
            documentRepository.delete(document);
        } catch (IOException e) {
            throw new CustomException("Failed to delete the file.", HttpStatus.BAD_REQUEST);
        }
    }

    public List<UserDocument> getDocumentsByUser(User user) {
        return documentRepository.findByUser(user);
    }

    public UserDocument getDocumentByType(String type, User user) {
        return documentRepository.findByTypeAndUser(type, user);
    }

    public UserDocument getDocumentByTypeIsNot(String type, User user) {
        return documentRepository.findByTypeIsNotAndUser(type, user);
    }

    private void deleteIfTypeExists(String type, User user) {
        if ("License".equals(type)) {
            UserDocument licenseDoc = getDocumentByType(type, user);
            if (licenseDoc != null) deleteUserDocument(licenseDoc.getId());
        } else {
            UserDocument document = getDocumentByTypeIsNot("License", user);
            if (document != null) deleteUserDocument(document.getId());
        }
    }
}
