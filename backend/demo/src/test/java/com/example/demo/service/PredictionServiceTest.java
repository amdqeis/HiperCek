package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.repository.InMemoryPredictionHistoryRepository;
import org.junit.jupiter.api.Test;

class PredictionServiceTest {
    @Test
    void determineCategoryUsesConfiguredThresholds() {
        PredictionService service = new PredictionService(null, new InMemoryPredictionHistoryRepository());

        assertThat(service.determineCategory(29.9)).isEqualTo("low");
        assertThat(service.determineCategory(30.0)).isEqualTo("medium");
        assertThat(service.determineCategory(69.9)).isEqualTo("medium");
        assertThat(service.determineCategory(70.0)).isEqualTo("high");
    }
}
