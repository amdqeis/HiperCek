package com.example.demo.dto;

import java.time.LocalDateTime;

public record PredictionResponseDto(
<<<<<<< HEAD
    String id,
    LocalDateTime createdAt,
    PredictionRiskDto hypertension,
    PredictionRiskDto cardiovascular
) {
}
=======
        String id,
        LocalDateTime createdAt,
        PredictionInputDto input,
        PredictionRiskDto hypertension,
        PredictionRiskDto cardiovascular
) {
}
>>>>>>> origin/Caca
