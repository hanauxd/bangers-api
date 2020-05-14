package lk.apiit.eirlss.bangerandco.jobs;

import lk.apiit.eirlss.bangerandco.dto.requests.AuthenticationRequest;
import lk.apiit.eirlss.bangerandco.dto.responses.DMVAuthResponse;
import lk.apiit.eirlss.bangerandco.models.ReportedLicense;
import lk.apiit.eirlss.bangerandco.services.ReportedLicenseService;
import lk.apiit.eirlss.bangerandco.web.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableScheduling
@ConditionalOnProperty(name = "app.enable.scheduling")
public class ReportedLicenseUpdater {
    private final Logger LOGGER = LoggerFactory.getLogger(ReportedLicenseUpdater.class);
    private final HttpClient client;
    private final ReportedLicenseService licenseService;
    @Value("${dmv.base-url}")
    private String DMV_BASE_URL;
    @Value("${dmv.username}")
    private String DMV_USERNAME;
    @Value("${dmv.password}")
    private String DMV_PASSWORD;
    @Value("${app.token.prefix}")
    private String TOKEN_PREFIX;

    @Autowired
    public ReportedLicenseUpdater(HttpClient client, ReportedLicenseService licenseService) {
        this.client = client;
        this.licenseService = licenseService;
    }

    @Scheduled(cron = "${cron.expression}")
    private void execute() {
        client.post(
                endpoint("login"),
                new AuthenticationRequest(DMV_USERNAME, DMV_PASSWORD),
                DMVAuthResponse.class,
                this::onAuthSuccess,
                this::onAuthFailure
        );
    }

    private void onAuthSuccess(Object res) {
        DMVAuthResponse authResponse = (DMVAuthResponse) res;
        client.get(
                endpoint("licenses"),
                token(authResponse.getJwt()),
                String.class,
                this::onLicenseSuccess,
                this::onLicenseFailure
        );
    }

    private void onLicenseSuccess(Object csv) {
        String csvString = (String) csv;
        licenseService.deleteAllInBatch();
        List<ReportedLicense> licenses = new ArrayList<>();
        String[] rows = csvString.split("\n");
        List<String> records = Arrays.stream(rows).skip(1).collect(Collectors.toList());
        for (String record : records) {
            String[] values = record.split(",");
            ReportedLicense license = new ReportedLicense(values[0], values[1], values[2]);
            licenses.add(license);
        }
        licenseService.saveAllInBatch(licenses);
    }

    private void onAuthFailure(Throwable throwable) {
        LOGGER.warn("[ON AUTH FAILURE] {}", throwable.getMessage());
        execute();
    }

    private void onLicenseFailure(Throwable throwable) {
        LOGGER.warn("[ON LICENSE FAILURE] {}", throwable.getMessage());
    }

    private String token(String jwt) {
        return TOKEN_PREFIX.concat(jwt);
    }

    private String endpoint(String endpoint) {
        return DMV_BASE_URL.concat(endpoint);
    }
}
