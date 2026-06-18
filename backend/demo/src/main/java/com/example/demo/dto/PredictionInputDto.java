package com.example.demo.dto;

public record PredictionInputDto(
        int age,
        double bmi,
        int systolicBp,
        int diastolicBp,
        boolean familyHistory,
        String smokingStatus,
        String physicalActivityLevel,
        boolean diabetes
) {
}