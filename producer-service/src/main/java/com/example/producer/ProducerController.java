package com.example.producer;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ProducerController {

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "producer-service");
        response.put("timestamp", LocalDateTime.now());
        return response;
    }

    @GetMapping("/data")
    public Map<String, Object> getData() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Hello from Producer Service!");
        response.put("data", "This is sample data from the producer");
        response.put("timestamp", LocalDateTime.now());
        response.put("service", "producer-service");
        return response;
    }

    @GetMapping("/data/{id}")
    public Map<String, Object> getDataById(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", id);
        response.put("message", "Data for ID: " + id);
        response.put("data", "Sample data item with ID: " + id);
        response.put("timestamp", LocalDateTime.now());
        response.put("service", "producer-service");
        return response;
    }
}
