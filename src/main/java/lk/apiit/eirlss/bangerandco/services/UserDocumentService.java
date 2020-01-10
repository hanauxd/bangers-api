package lk.apiit.eirlss.bangerandco.services;

import lk.apiit.eirlss.bangerandco.exceptions.CustomException;
import lk.apiit.eirlss.bangerandco.models.User;
import lk.apiit.eirlss.bangerandco.models.UserDocument;
import lk.apiit.eirlss.bangerandco.repositories.UserDocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDocumentService {

    private UserDocumentRepository documentRepository;

    @Autowired
    public UserDocumentService(UserDocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public UserDocument getUserDocumentById(String id) {
        return documentRepository.findById(id).orElseThrow(() -> new CustomException("Document not found.", HttpStatus.NOT_FOUND));
    }

    public UserDocument createUserDocument(UserDocument document) {
        return documentRepository.save(document);
    }

    public void deleteUserDocument(String id) {
        UserDocument document = getUserDocumentById(id);
        document.getUser().removeUserDocument(document);
        documentRepository.delete(document);
    }

    public List<UserDocument> getDocumentsByUser(User user) {
        return documentRepository.findByUser(user);
    }
}
