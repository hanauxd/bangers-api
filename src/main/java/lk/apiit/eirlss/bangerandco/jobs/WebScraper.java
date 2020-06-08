package lk.apiit.eirlss.bangerandco.jobs;

import lk.apiit.eirlss.bangerandco.services.ExternalRateService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static lk.apiit.eirlss.bangerandco.jobs.Constants.*;

@Service
public class WebScraper {
    private final Logger LOGGER = LoggerFactory.getLogger(WebScraper.class);
    private final ExternalRateService externalRateService;

    @Autowired
    public WebScraper(ExternalRateService externalRateService) {
        this.externalRateService = externalRateService;
    }

    @Scheduled(cron = "${cron.expression}")
    public void scrape() {
        try {
            Document document = Jsoup.connect(MALKEY_URL).get();
            Elements vehicles = document.select(CSS_QUERY_VEHICLES);
            Elements rates = document.select(CSS_QUERY_RATES);
            externalRateService.persistExternalRates(vehicles, rates);
        } catch (IOException e) {
            LOGGER.warn("Failed to get html document. {}", e.getMessage());
        }
    }
}
