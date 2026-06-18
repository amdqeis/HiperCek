package com.example.demo.dto;

import java.time.LocalDateTime;

public record PredictionHistoryItemDto(
        String id,
        LocalDateTime createdAt,
        PredictionInputDto input,
        PredictionRiskDto hypertension,
        PredictionRiskDto cardiovascular
) {
}
