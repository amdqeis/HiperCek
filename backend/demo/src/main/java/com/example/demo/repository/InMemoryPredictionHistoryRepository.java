package com.example.demo.repository;

import com.example.demo.model.RiwayatPrediksi;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryPredictionHistoryRepository implements PredictionHistoryRepository {
    private final ConcurrentHashMap<String, RiwayatPrediksi> storage = new ConcurrentHashMap<>();

    @Override
    public RiwayatPrediksi save(RiwayatPrediksi item) {
        storage.put(item.getId(), item);
        return item;
    }

    @Override
    public List<RiwayatPrediksi> findAll() {
        List<RiwayatPrediksi> items = new ArrayList<>(storage.values());
        items.sort(Comparator.comparing(RiwayatPrediksi::getWaktuTambah).reversed());
        return items;
    }

    @Override
    public Optional<RiwayatPrediksi> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public void deleteById(String id) {
        storage.remove(id);
    }
}
