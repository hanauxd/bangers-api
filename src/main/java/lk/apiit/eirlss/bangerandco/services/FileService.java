package lk.apiit.eirlss.bangerandco.services;

import lk.apiit.eirlss.bangerandco.exceptions.CustomException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
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
    @Value("${app.document.store-location}")
    private String documentLocation;
    String workingDirectory = System.getProperty("user.dir");

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

    public Path getPath(String filename) {
        String path = workingDirectory + File.separator + documentLocation + File.separator + filename;
        return Paths.get(path);
    }

    public String getPathString(String filename) {
        return workingDirectory + File.separator + documentLocation + File.separator + filename;
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

    public ResponseEntity<?> downloadFile(String filename, HttpServletRequest request) {
        try {
            UrlResource resource = getResource(filename);
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

    private UrlResource getResource(String filename) {
        try {
            Path path = getPath(filename);
            UrlResource resource = new UrlResource(path.toUri());
            if (!resource.exists()) throw new CustomException("File not found.", HttpStatus.NOT_FOUND);
            return resource;
        } catch (MalformedURLException e) {
            throw new CustomException("Failed to get the file path.", HttpStatus.BAD_REQUEST);
        }
    }
}