package com.example.demo.service;

import com.example.demo.exception.MlServiceException;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class MlApiClient {
    private final RestTemplate restTemplate;
    private final String baseUrl;

    public MlApiClient(RestTemplate restTemplate, @Value("${ml.api.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public double predictHypertension(Map<String, Object> payload) {
        return postForProbability("/predict", payload);
    }

    public double predictCardiovascular(Map<String, Object> payload) {
        return postForProbability("/predict/cardiovascular", payload);
    }

    private double postForProbability(String path, Map<String, Object> payload) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            Double response = restTemplate.postForObject(
                baseUrl + path,
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
}
