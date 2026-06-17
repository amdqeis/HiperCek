package com.example.demo.controller;

import com.example.demo.dto.PredictionHistoryItemDto;
import com.example.demo.dto.PredictionRequestDto;
import com.example.demo.dto.PredictionResponseDto;
import com.example.demo.service.PredictionService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/predictions")
public class PredictionController {
    private final PredictionService predictionService;

    public PredictionController(PredictionService predictionService) {
        this.predictionService = predictionService;
    }

    @PostMapping
    public ResponseEntity<PredictionResponseDto> createPrediction(@Valid @RequestBody PredictionRequestDto request) {
        return ResponseEntity.ok(predictionService.predict(request));
    }

    @GetMapping("/history")
    public ResponseEntity<List<PredictionHistoryItemDto>> getHistory() {
        return ResponseEntity.ok(predictionService.listHistory());
    }

    @DeleteMapping("/history/{id}")
    public ResponseEntity<Void> deleteHistory(@PathVariable String id) {
        predictionService.deleteHistory(id);
        return ResponseEntity.noContent().build();
    }
}
