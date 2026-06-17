package com.example.demo.dto;

import java.time.LocalDateTime;

public record PredictionResponseDto(
        String id,
        LocalDateTime createdAt,
        PredictionInputDto input,
        PredictionRiskDto hypertension,
        PredictionRiskDto cardiovascular
) {
}