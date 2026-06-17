package com.example.demo.service;

import com.example.demo.model.HealthData;
import com.example.demo.model.HasilPrediksi;

public abstract class MachineLearningService {
    private String apiUrl;

    public MachineLearningService(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public abstract HasilPrediksi predict(HealthData data);
}
