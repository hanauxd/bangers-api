package lk.apiit.eirlss.bangerandco.dmv.jobs;

import lk.apiit.eirlss.bangerandco.dmv.models.AuthResponse;
import lk.apiit.eirlss.bangerandco.dto.requests.AuthenticationRequest;
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
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Configuration
@EnableScheduling
@ConditionalOnProperty(name = "app.enable.scheduling")
public class ReportedLicenseUpdater {
    private final Logger LOGGER = LoggerFactory.getLogger(ReportedLicenseUpdater.class);
    @Value("${dmv.base-url}")
    private String DMV_BASE_URL;
    @Value("${dmv.username}")
    private String DMV_USERNAME;
    @Value("${dmv.password}")
    private String DMV_PASSWORD;
    @Value("${app.token.prefix}")
    private String TOKEN_PREFIX;
    private HttpClient client;
    private ReportedLicenseService licenseService;
    private final Consumer<String> licenseCallback = csv -> {
        licenseService.deleteAllInBatch();
        List<ReportedLicense> licenses = new ArrayList<>();
        String[] rows = csv.split("\n");
        List<String> records = Arrays.stream(rows).skip(1).collect(Collectors.toList());
        for (String record : records) {
            String[] values = record.split(",");
            ReportedLicense license = new ReportedLicense(values[0], values[1], values[2]);
            licenses.add(license);
        }
        licenseService.saveAllInBatch(licenses);
    };
    private final Consumer<AuthResponse> authCallback = auth -> {
        client.get(
                endpoint("licenses"),
                token(auth.getJwt()),
                String.class,
                licenseCallback
        );
    };
    private final Consumer<Throwable> errorCallback = throwable -> {
        LOGGER.warn("[ON AUTH FAILURE] {}", throwable.getMessage());
        execute();
    };

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
                AuthResponse.class,
                authCallback,
                errorCallback
        );
    }

    private String token(String jwt) {
        return TOKEN_PREFIX.concat(jwt);
    }

    private String endpoint(String endpoint) {
        return DMV_BASE_URL.concat(endpoint);
    }
}
