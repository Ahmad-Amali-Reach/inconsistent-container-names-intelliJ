package com.example.consumer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ConsumerController {

    private final WebClient webClient;
    
    @Value("${producer.service.url:http://producer-service:8080}")
    private String producerServiceUrl;

    public ConsumerController(WebClient webClient) {
        this.webClient = webClient;
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "consumer-service");
        response.put("timestamp", LocalDateTime.now());
        response.put("producer-url", producerServiceUrl);
        return response;
    }

    @GetMapping("/consume")
    public Mono<Map<String, Object>> consumeData() {
        return webClient.get()
                .uri(producerServiceUrl + "/api/data")
                .retrieve()
                .bodyToMono(Map.class)
                .map(producerResponse -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("consumer-service", "consumer-service");
                    response.put("timestamp", LocalDateTime.now());
                    response.put("producer-response", producerResponse);
                    response.put("message", "Successfully consumed data from producer service");
                    return response;
                })
                .onErrorReturn(createErrorResponse("Failed to connect to producer service"));
    }

    @GetMapping("/consume/{id}")
    public Mono<Map<String, Object>> consumeDataById(@PathVariable String id) {
        return webClient.get()
                .uri(producerServiceUrl + "/api/data/" + id)
                .retrieve()
                .bodyToMono(Map.class)
                .map(producerResponse -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("consumer-service", "consumer-service");
                    response.put("timestamp", LocalDateTime.now());
                    response.put("requested-id", id);
                    response.put("producer-response", producerResponse);
                    response.put("message", "Successfully consumed data for ID: " + id);
                    return response;
                })
                .onErrorReturn(createErrorResponse("Failed to get data for ID: " + id));
    }

    @GetMapping("/check-producer")
    public Mono<Map<String, Object>> checkProducerHealth() {
        return webClient.get()
                .uri(producerServiceUrl + "/api/health")
                .retrieve()
                .bodyToMono(Map.class)
                .map(producerResponse -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("consumer-service", "consumer-service");
                    response.put("producer-health", producerResponse);
                    response.put("connection-status", "SUCCESS");
                    response.put("timestamp", LocalDateTime.now());
                    return response;
                })
                .onErrorReturn(createErrorResponse("Producer service is not reachable"));
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("consumer-service", "consumer-service");
        errorResponse.put("error", message);
        errorResponse.put("producer-url", producerServiceUrl);
        errorResponse.put("timestamp", LocalDateTime.now());
        return errorResponse;
    }
}
