package lk.apiit.eirlss.bangerandco.services;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lk.apiit.eirlss.bangerandco.exceptions.CustomException;
import lk.apiit.eirlss.bangerandco.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class MailService {
    private final Logger LOGGER = LoggerFactory.getLogger(MailService.class);
    private final JavaMailSender sender;
    private final Configuration configuration;
    private final FileService fileService;

    @Value("${dmv.registration-number}")
    private String registrationNumber;
    @Value("${dmv.email}")
    private String dmvEmail;

    @Autowired
    public MailService(JavaMailSender sender, Configuration configuration, FileService fileService) {
        this.sender = sender;
        this.configuration = configuration;
        this.fileService = fileService;
    }

    public void sendMailWithAttachment(User user, String filename) {
        MimeMessage message = createMimeMessage(sender.createMimeMessage(), user, getAttachment(filename));
        sender.send(message);
        LOGGER.debug("Mail sent successfully.");
    }

    private MimeMessage createMimeMessage(MimeMessage message, User user, FileSystemResource file) {
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(dmvEmail);
            helper.setSubject("Reported License");
            helper.setText(htmlContent(user), true);
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

    private String htmlContent(User user) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("EEEEE, dd MMM yyyy HH:mm aa");
            String date = format.format(new Date());
            Map<String, String> model = new HashMap<>();
            model.put("registrationNumber", registrationNumber.toUpperCase());
            model.put("date", date);
            model.put("customerName", user.getFullName());
            model.put("license", user.getLicense());
            model.put("nic", user.getNic());

            configuration.setClassForTemplateLoading(this.getClass(), "/templates");
            Template template = configuration.getTemplate("email-template.ftl");
            return FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
        } catch (IOException | TemplateException e) {
            LOGGER.warn("Failed to load email template. Error message: {}", e.getMessage());
            throw new CustomException("Failed to load email template", HttpStatus.BAD_REQUEST);
        }
    }
}
