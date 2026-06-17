package com.example.demo.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record PredictionRequestDto(
    @NotNull @Min(1) @Max(120) Integer age,
    @NotNull @DecimalMin("10.0") @DecimalMax("70.0") Double bmi,
    @NotNull @Min(70) @Max(250) Integer systolicBp,
    @NotNull @Min(40) @Max(150) Integer diastolicBp,
    @NotNull Boolean familyHistory,
    @NotBlank @Pattern(regexp = "Never|Former|Current") String smokingStatus,
    @NotBlank @Pattern(regexp = "Low|Moderate|High") String physicalActivityLevel,
    @NotNull Boolean diabetes
) {
}
