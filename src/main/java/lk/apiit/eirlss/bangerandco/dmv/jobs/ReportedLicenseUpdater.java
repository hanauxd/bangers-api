package lk.apiit.eirlss.bangerandco.dmv.jobs;

import lk.apiit.eirlss.bangerandco.dmv.models.AuthResponse;
import lk.apiit.eirlss.bangerandco.dmv.web.HttpClient;
import lk.apiit.eirlss.bangerandco.dto.requests.AuthenticationRequest;
import lk.apiit.eirlss.bangerandco.models.ReportedLicense;
import lk.apiit.eirlss.bangerandco.services.ReportedLicenseService;
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
    @Value("${dmv.base-url}")
    private String baseURL;
    @Value("${dmv.username}")
    private String username;
    @Value("${dmv.password}")
    private String password;
    private HttpClient client;
    private final ReportedLicenseService licenseService;

    @Autowired
    public ReportedLicenseUpdater(HttpClient client, ReportedLicenseService licenseService) {
        this.client = client;
        this.licenseService = licenseService;
    }

    @Scheduled(cron = "${cron.expression}")
    private void execute() {
        client.post(
                baseURL.concat("login"),
                new AuthenticationRequest(username, password),
                AuthResponse.class,
                authCallback,
                errorCallback
        );
    }

    private final Consumer<AuthResponse> authCallback = auth -> {
        Consumer<String> licenseCallback = this::updateDatabase;
        client.get(
                baseURL.concat("licenses"),
                getToken(auth.getJwt()),
                String.class,
                licenseCallback
        );
    };

    private final Consumer<Throwable> errorCallback = throwable ->
            System.out.println("[ON AUTH FAILURE] ".concat(throwable.getMessage()));

    private void updateDatabase(String csv) {
        licenseService.deleteAllInBatch();
        List<ReportedLicense> licenses = new ArrayList<>();
        String[] rows = csv.split("\n");
        List<String> records = Arrays.stream(rows).skip(1).collect(Collectors.toList());
        for (String column : records) {
            String[] values = column.split(",");
            ReportedLicense license = new ReportedLicense(values[0], values[1], values[2]);
            licenses.add(license);
        }
        licenseService.saveAllInBatch(licenses);
    }

    private String getToken(String jwt) {
        return "Bearer ".concat(jwt);
    }
}
