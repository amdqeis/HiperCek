package com.example.demo.service;

import com.example.demo.model.HasilPrediksi;
import com.example.demo.model.HealthData;
import com.example.demo.model.RiwayatPrediksi;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class HypertensionAIService extends MachineLearningService {

    public HypertensionAIService(RestTemplate restTemplate, @Value("${ml.api.base-url}") String apiUrl) {
        super(restTemplate, apiUrl);
    }

    @Override
    public HasilPrediksi predict(HealthData data) {
        double percentage = postForProbability("/predict", data.toHypertensionLevel());
        String category = determineCategory(percentage);
        HasilPrediksi hasil = new HasilPrediksi(percentage, category, buildSaran(category, data));
        hasil.setCatatan(buildCatatan(category, data));
        return hasil;
    }

    public double parseResponse(String response) {
        try {
            return Double.parseDouble(response);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    public String tentukanKategori(int sistolik, int diastolik) {
        if (sistolik >= 140 || diastolik >= 90) return "high";
        if (sistolik >= 120 || diastolik >= 80) return "medium";
        return "low";
    }

    public String generateSaran(String kategoriRisiko) {
        return switch (kategoriRisiko) {
            case "high" -> "Segera jadwalkan konsultasi dengan dokter untuk evaluasi tekanan darah.";
            case "medium" -> "Batasi konsumsi garam harian dan lakukan olahraga aerobik ringan.";
            default -> "Pertahankan aktivitas fisik teratur dan pola makan seimbang.";
        };
    }

    public void simpanHasil(HasilPrediksi hasil, RiwayatPrediksi riwayat) {
        // Penyimpanan ke database ditangani oleh PredictionService melalui repository
    }

    private String buildCatatan(String category, HealthData data) {
        return switch (category) {
            case "low" -> "Tekanan darah Anda masih dalam rentang yang relatif aman saat ini.";
            case "medium" -> "Tekanan darah mulai menunjukkan pola yang perlu dipantau secara rutin.";
            default -> data.getSistolik() >= 140
                ? "Indikasi tekanan darah tinggi cukup kuat. Konsultasi medis disarankan."
                : "Risiko hipertensi tinggi terdeteksi dan perlu evaluasi lebih lanjut.";
        };
    }

    private List<String> buildSaran(String category, HealthData data) {
        if ("high".equals(category)) {
            return List.of(
                data.getSistolik() >= 140
                    ? "Segera jadwalkan konsultasi dengan dokter untuk evaluasi tekanan darah."
                    : "Konsultasikan hasil ini ke tenaga medis untuk pemeriksaan lanjutan.",
                "Kurangi rokok, perbaiki pola tidur, dan pantau tekanan darah secara berkala."
            );
        }
        if ("medium".equals(category)) {
            return List.of(
                "Batasi konsumsi garam harian dan perbanyak makanan segar.",
                "Lakukan olahraga aerobik ringan minimal 30 menit per hari."
            );
        }
        return List.of(
            "Pertahankan aktivitas fisik teratur setiap minggu.",
            "Lanjutkan pola makan seimbang dengan garam dan gula terkontrol."
        );
    }
}
