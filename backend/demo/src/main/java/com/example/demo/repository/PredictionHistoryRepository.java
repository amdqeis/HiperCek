package com.example.demo.repository;

import com.example.demo.model.RiwayatPrediksi;
import java.util.List;
import java.util.Optional;

public interface PredictionHistoryRepository {
    RiwayatPrediksi save(RiwayatPrediksi item);
    List<RiwayatPrediksi> findAll();
    Optional<RiwayatPrediksi> findById(String id);
    void deleteById(String id);
}
