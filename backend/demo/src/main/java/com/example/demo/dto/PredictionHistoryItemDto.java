package com.example.demo.dto;

import java.time.LocalDateTime;

public record PredictionHistoryItemDto(
    String id,
    LocalDateTime createdAt,
    PredictionRiskDto hypertension,
    PredictionRiskDto cardiovascular
) {
}
