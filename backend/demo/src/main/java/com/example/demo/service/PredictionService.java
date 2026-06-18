package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

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

@Service
public class PredictionService {
    private final MlApiClient mlApiClient;
    private final PredictionHistoryRepository historyRepository;

    public PredictionService(MlApiClient mlApiClient, PredictionHistoryRepository historyRepository) {
        this.mlApiClient = mlApiClient;
        this.historyRepository = historyRepository;
    }

    public PredictionResponseDto predict(PredictionRequestDto request) {
        validateBloodPressure(request);
        HealthData healthData = toHealthData(request);
        if (!healthData.isValid()) {
            throw new IllegalArgumentException("Data kesehatan tidak valid.");
        }

        double hypertensionPercentage = mlApiClient.predictHypertension(healthData.toHypertensionLevel());
        double cardiovascularPercentage = mlApiClient.predictCardiovascular(healthData.toCardiovascularLevel());

        HasilPrediksi hypertension = buildRisk("hypertension", hypertensionPercentage, healthData);
        HasilPrediksi cardiovascular = buildRisk("cardiovascular", cardiovascularPercentage, healthData);

        RiwayatPrediksi saved = historyRepository.save(new RiwayatPrediksi(healthData, new HasilPrediksi[]{hypertension, cardiovascular}));
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

    private HasilPrediksi buildRisk(String riskType, double percentage, HealthData healthData) {
        String category = determineCategory(percentage);
        String note = buildNote(riskType, category, healthData);
        List<String> suggestions = buildSuggestions(category, healthData);
        HasilPrediksi hasil = new HasilPrediksi(percentage, category, suggestions);
        hasil.setCatatan(note);
        return hasil;
    }

    String determineCategory(double percentage) {
        if (percentage < 30.0) {
            return "low";
        }
        if (percentage < 70.0) {
            return "medium";
        }
        return "high";
    }

    private String buildNote(String riskType, String category, HealthData healthData) {
        if ("hypertension".equals(riskType)) {
            return switch (category) {
                case "low" -> "Tekanan darah Anda masih dalam rentang yang relatif aman saat ini.";
                case "medium" -> "Tekanan darah mulai menunjukkan pola yang perlu dipantau secara rutin.";
                default -> healthData.getSistolik() >= 140
                    ? "Indikasi tekanan darah tinggi cukup kuat. Konsultasi medis disarankan."
                    : "Risiko hipertensi tinggi terdeteksi dan perlu evaluasi lebih lanjut.";
            };
        }

        return switch (category) {
            case "low" -> "Parameter kardiovaskular Anda belum menunjukkan risiko menonjol.";
            case "medium" -> "Ada beberapa parameter yang meningkatkan risiko kardiovaskular moderat.";
            default -> "Beberapa indikator utama menunjukkan risiko kardiovaskular yang perlu diwaspadai.";
        };
    }

    private List<String> buildSuggestions(String category, HealthData healthData) {
        if ("low".equals(category)) {
            return List.of(
                "Pertahankan aktivitas fisik teratur setiap minggu.",
                "Lanjutkan pola makan seimbang dengan garam dan gula terkontrol."
            );
        }
        if ("medium".equals(category)) {
            return List.of(
                "Batasi konsumsi garam harian dan perbanyak makanan segar.",
                "Lakukan olahraga aerobik ringan minimal 30 menit per hari."
            );
        }
        return List.of(
            healthData.getSistolik() >= 140
                ? "Segera jadwalkan konsultasi dengan dokter untuk evaluasi tekanan darah."
                : "Konsultasikan hasil ini ke tenaga medis untuk pemeriksaan lanjutan.",
            "Kurangi rokok, perbaiki pola tidur, dan pantau tekanan darah secara berkala."
        );
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
        if (healthData == null) {
            return null;
        }

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
