package lk.apiit.eirlss.bangerandco.services;

import lk.apiit.eirlss.bangerandco.exceptions.CustomException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.Date;

@Service
public class MailService {
    private final Logger LOGGER = LoggerFactory.getLogger(MailService.class);
    private final JavaMailSender sender;
    private final FileService fileService;

    @Value("${dmv.registration-number}")
    private String registrationNumber;
    @Value("${dmv.email}")
    private String dmvEmail;

    @Autowired
    public MailService(JavaMailSender sender, FileService fileService) {
        this.sender = sender;
        this.fileService = fileService;
    }

    public void sendMailWithAttachment(String filename) {
        MimeMessage message = createMimeMessage(sender.createMimeMessage(), getAttachment(filename));
        sender.send(message);
        LOGGER.debug("Mail sent successfully.");
    }

    private MimeMessage createMimeMessage(MimeMessage message, FileSystemResource file) {
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(dmvEmail);
            helper.setSubject("Reported License");
            helper.setText(String.format("Registration Number: %s\nDate: %s", registrationNumber, new Date()));
            helper.addAttachment(file.getFilename(), file);
            return helper.getMimeMessage();
        } catch (MessagingException e) {
            LOGGER.warn("Failed to construct MimeMessageHelper. Error message: {}", e.getMessage());
            throw new CustomException("Failed to construct message helper", HttpStatus.BAD_REQUEST);
        }
    }

    private FileSystemResource getAttachment(String filename) {
        String path = fileService.getPathString(filename);
        return new FileSystemResource(new File(path));
    }
}
