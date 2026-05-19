package com.smartcut.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.smartcut.model.dto.EngineRequest;
import com.smartcut.model.dto.EngineResponse;

public class EngineClient {

    private final String baseUrl;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public EngineClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();
        this.objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public boolean healthCheck() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/health"))
                .GET()
                .build();
            HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    public EngineResponse submitTask(EngineRequest taskRequest) {
        try {
            String json = objectMapper.writeValueAsString(taskRequest);
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/task"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
            HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());
            return objectMapper.readValue(response.body(), EngineResponse.class);
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            return EngineResponse.error("Engine communication failed: " + e.getMessage());
        }
    }
}
