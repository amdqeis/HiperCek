package com.example.demo.service;

import com.example.demo.dto.PredictionHistoryItemDto;
import com.example.demo.dto.PredictionInputDto;
import com.example.demo.dto.PredictionRequestDto;
import com.example.demo.dto.PredictionResponseDto;
import com.example.demo.dto.PredictionRiskDto;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.HasilPrediksi;
import com.example.demo.model.HealthData;
import com.example.demo.model.RiwayatPrediksi;
import com.example.demo.repository.PredictionHistoryRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PredictionService {
    private final HypertensionAIService hypertensionService;
    private final CardiovascularAIService cardiovascularService;
    private final PredictionHistoryRepository historyRepository;

    public PredictionService(
        HypertensionAIService hypertensionService,
        CardiovascularAIService cardiovascularService,
        PredictionHistoryRepository historyRepository
    ) {
        this.hypertensionService = hypertensionService;
        this.cardiovascularService = cardiovascularService;
        this.historyRepository = historyRepository;
    }

    public PredictionResponseDto predict(PredictionRequestDto request) {
        validateBloodPressure(request);
        HealthData healthData = toHealthData(request);
        if (!healthData.isValid()) {
            throw new IllegalArgumentException("Data kesehatan tidak valid.");
        }

        HasilPrediksi hypertension = hypertensionService.predict(healthData);
        HasilPrediksi cardiovascular = cardiovascularService.predict(healthData);

        RiwayatPrediksi saved = historyRepository.save(
            new RiwayatPrediksi(healthData, new HasilPrediksi[]{hypertension, cardiovascular})
        );
        return toResponse(saved);
    }

    public List<PredictionHistoryItemDto> listHistory() {
        return historyRepository.findAll().stream()
            .map(this::toHistoryItem)
            .toList();
    }

    public void deleteHistory(String id) {
        RiwayatPrediksi existing = historyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Riwayat prediksi tidak ditemukan."));
        historyRepository.deleteById(existing.getId());
    }

    // Package-private: digunakan oleh PredictionServiceTest
    String determineCategory(double percentage) {
        if (percentage < 30.0) return "low";
        if (percentage < 70.0) return "medium";
        return "high";
    }

    private void validateBloodPressure(PredictionRequestDto request) {
        if (request.diastolicBp() >= request.systolicBp()) {
            throw new IllegalArgumentException("Tekanan diastolik harus lebih kecil dari sistolik.");
        }
    }

    private HealthData toHealthData(PredictionRequestDto request) {
        HealthData healthData = new HealthData();
        healthData.setUsia(request.age());
        healthData.setBmi(request.bmi());
        healthData.setSistolik(request.systolicBp());
        healthData.setDiastolik(request.diastolicBp());
        healthData.setRiwayatKeluarga(request.familyHistory());
        healthData.setMerokok(request.smokingStatus());
        healthData.setAktivitasFisik(switch (request.physicalActivityLevel()) {
            case "Low" -> 0;
            case "Moderate" -> 1;
            case "High" -> 2;
            default -> throw new IllegalArgumentException("Aktivitas fisik tidak valid.");
        });
        healthData.setDiabetes(request.diabetes());
        return healthData;
    }

    private PredictionResponseDto toResponse(RiwayatPrediksi item) {
        return new PredictionResponseDto(
            item.getId(),
            item.getWaktuTambah(),
            toInputDto(item.getHealthData()),
            toRiskDto(item.getHipertensi()),
            toRiskDto(item.getKardiovaskular())
        );
    }

    private PredictionHistoryItemDto toHistoryItem(RiwayatPrediksi item) {
        return new PredictionHistoryItemDto(
            item.getId(),
            item.getWaktuTambah(),
            toInputDto(item.getHealthData()),
            toRiskDto(item.getHipertensi()),
            toRiskDto(item.getKardiovaskular())
        );
    }

    private PredictionInputDto toInputDto(HealthData healthData) {
        if (healthData == null) return null;
        return new PredictionInputDto(
            healthData.getUsia(),
            healthData.getBmi(),
            healthData.getSistolik(),
            healthData.getDiastolik(),
            healthData.isRiwayatKeluarga(),
            healthData.getMerokok(),
            healthData.getPhysicalActivityLevel(),
            healthData.isDiabetes()
        );
    }

    private PredictionRiskDto toRiskDto(HasilPrediksi hasil) {
        return new PredictionRiskDto(
            hasil.getPersentaseRisiko(),
            hasil.getKategoriRisiko(),
            hasil.getCatatan(),
            hasil.getSaran()
        );
    }
}
