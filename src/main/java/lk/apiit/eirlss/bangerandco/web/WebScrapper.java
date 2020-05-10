package lk.apiit.eirlss.bangerandco.web;

import lk.apiit.eirlss.bangerandco.models.ExternalRate;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class WebScrapper {
    private final Logger LOGGER = LoggerFactory.getLogger(WebScrapper.class);
    private static final String URL = "https://www.malkey.lk/rates/with-driver-rates.html";

    public WebScrapper() {
        new Thread(this::scrape).start();
    }

    public void scrape() {
        try {
            Document document = Jsoup.connect(URL).get();
            Elements elVehicle = document.select("td.text-left.percent-60");
            Elements elPrice = document.select("td.text-center.percent-17");
            int j = 0;
            for (Element element : elVehicle) {
                double price = Double.parseDouble(elPrice.get(j).text().replace(",",""));
                String vehicle = element.text();
                ExternalRate rate = new ExternalRate(vehicle, price);
                System.out.println(rate);
                j = j + 2;
            }
        } catch (IOException e) {
            LOGGER.warn("Failed to get document. {}", e.getMessage());
        }
    }
}
