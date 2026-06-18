package com.example.demo.service;

import com.example.demo.exception.MlServiceException;
import com.example.demo.model.HasilPrediksi;
import com.example.demo.model.HealthData;
import java.util.Map;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public abstract class MachineLearningService {
    private final RestTemplate restTemplate;
    private final String apiUrl;

    protected MachineLearningService(RestTemplate restTemplate, String apiUrl) {
        this.restTemplate = restTemplate;
        this.apiUrl = apiUrl;
    }

    public abstract HasilPrediksi predict(HealthData data);

    protected double postForProbability(String path, Map<String, Object> payload) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            Double response = restTemplate.postForObject(
                apiUrl + path,
                new HttpEntity<>(payload, headers),
                Double.class
            );
            if (response == null) {
                throw new MlServiceException("ML API mengembalikan respons kosong.");
            }
            return response;
        } catch (RestClientException exception) {
            throw new MlServiceException("Gagal menghubungi ML API.", exception);
        }
    }

    protected String determineCategory(double percentage) {
        if (percentage < 30.0) return "low";
        if (percentage < 70.0) return "medium";
        return "high";
    }
}
