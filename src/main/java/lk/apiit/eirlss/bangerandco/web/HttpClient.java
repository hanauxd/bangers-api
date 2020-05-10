package lk.apiit.eirlss.bangerandco.web;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.function.Consumer;

@Service
public class HttpClient {
    private final WebClient client;

    public HttpClient() {
        this.client = WebClient.builder().build();
    }

    public void post(String endpoint, Object body, Class<?> type, Consumer onSuccess, Consumer<Throwable> onError) {
        client.post()
                .uri(endpoint)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(type)
                .subscribe(onSuccess, onError);
    }

    public void get(String endpoint, String token, Class<?> type, Consumer onSuccess) {
        client.get()
                .uri(endpoint)
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .bodyToMono(type)
                .subscribe(onSuccess);
    }
}
