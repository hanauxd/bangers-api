package lk.apiit.eirlss.bangerandco.jobs;

import lk.apiit.eirlss.bangerandco.services.ExternalRateService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class WebScraper {
    private static final String URL = "https://www.malkey.lk/rates/with-driver-rates.html";
    private final Logger LOGGER = LoggerFactory.getLogger(WebScraper.class);
    private final ExternalRateService externalRateService;

    @Autowired
    public WebScraper(ExternalRateService externalRateService) {
        this.externalRateService = externalRateService;
    }

    @Scheduled(cron = "${cron.expression}")
    public void scrape() {
        try {
            Document document = Jsoup.connect(URL).get();
            Elements vehicles = document.select("td.text-left.percent-60");
            Elements rates = document.select("td.text-center.percent-17");
            persistExternalRates(vehicles, rates);
        } catch (IOException e) {
            LOGGER.warn("Failed to get html document. {}", e.getMessage());
        }
    }

    private void persistExternalRates(Elements vehicles, Elements rates) {
        externalRateService.deleteAllInBatch();
        int j = 0;
        for (Element vehicle : vehicles) {
            externalRateService.createExternalRate(
                    vehicle.text(),
                    rate(rates.get(j))
            );
            j = j + 2;
        }
    }

    private double rate(Element rate) {
        return Double.parseDouble(rate.text().replace(",", ""));
    }
}
