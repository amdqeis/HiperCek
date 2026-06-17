package com.example.demo.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataPredictionHistoryRepository extends JpaRepository<PredictionHistoryEntity, String> {
    List<PredictionHistoryEntity> findAllByOrderByWaktuTambahDesc();
}
