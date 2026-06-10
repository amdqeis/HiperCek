package com.example.demo.repository;

import com.example.demo.model.RiwayatPrediksi;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class DatabasePredictionHistoryRepository implements PredictionHistoryRepository {
    private final SpringDataPredictionHistoryRepository repository;

    public DatabasePredictionHistoryRepository(SpringDataPredictionHistoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public RiwayatPrediksi save(RiwayatPrediksi item) {
        return repository.save(PredictionHistoryEntity.fromDomain(item)).toDomain();
    }

    @Override
    public List<RiwayatPrediksi> findAll() {
        return repository.findAllByOrderByWaktuTambahDesc().stream()
            .map(PredictionHistoryEntity::toDomain)
            .toList();
    }

    @Override
    public Optional<RiwayatPrediksi> findById(String id) {
        return repository.findById(id).map(PredictionHistoryEntity::toDomain);
    }

    @Override
    public void deleteById(String id) {
        repository.deleteById(id);
    }
}
