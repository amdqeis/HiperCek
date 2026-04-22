package com.example.demo.service;

import com.example.demo.model.HasilPrediksi;
import com.example.demo.model.HealthData;

public abstract class MachineLearningServices {
    protected String apiURL;

    public MachineLearningServices(String apiURL) {
        this.apiURL = apiURL;
    }

    public String getApiURL() {
        return apiURL;
    }

    public void setApiURL(String apiURL) {
        this.apiURL = apiURL;
    }

    public abstract HasilPrediksi predict(HealthData healthData);
}
