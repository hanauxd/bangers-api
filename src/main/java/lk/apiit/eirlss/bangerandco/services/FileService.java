package lk.apiit.eirlss.bangerandco.services;

import lk.apiit.eirlss.bangerandco.exceptions.CustomException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileService {
    @Value("${app.path.user-documents}")
    private String documentLocation;

    public String store(MultipartFile file) {
        try {
            String filename = rename(file.getOriginalFilename());
            Path path = getPath(filename);
            checkContentType(path);
            if (!Files.exists(path)) Files.createDirectories(path);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            return filename;
        } catch (IOException e) {
            throw new CustomException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    public UrlResource getResource(String filename) {
        try {
            Path path = getPath(filename);
            UrlResource resource = new UrlResource(path.toUri());
            if (!resource.exists()) throw new CustomException("File not found.", HttpStatus.NOT_FOUND);
            return resource;
        } catch (MalformedURLException e) {
            throw new CustomException("Failed to get the file path.", HttpStatus.BAD_REQUEST);
        }
    }

    public Path getPath(String filename) {
        String path = documentLocation + File.separator + filename;
        return Paths.get(path);
    }

    private String rename(String filename) {
        String uuid = UUID.randomUUID().toString();
        String fileExtension = getFileExtension(filename);
        return uuid.concat(fileExtension);
    }

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex);
    }

    private void checkContentType(Path path) {
        try {
            String contentType = Files.probeContentType(path);
            if (!contentType.equals("image/jpeg") && !contentType.equals("image/png"))
                throw new CustomException("Invalid file type.", HttpStatus.BAD_REQUEST);
        } catch (IOException e) {
            throw new CustomException("Failed to detect file type.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}