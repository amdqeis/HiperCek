package com.example.demo.dto;

import java.util.List;

public record PredictionRiskDto(
    double percentage,
    String category,
    String note,
    List<String> suggestions
) {
}
