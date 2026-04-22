package com.example.demo.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.example.demo.model.HasilPrediksi;
import com.example.demo.model.HealthData;

@Service
public class HypertensionAIService extends MachineLearningService {

    public HypertensionAIService(String apiUrl) {
        super(apiUrl);
    }

    @Override
    public HasilPrediksi predict(HealthData data) {
        if (!data.isValid()) {
            throw new IllegalArgumentException("Data kesehatan tidak valid.");
        }

        String response = kirimKeApi(data.toJson());
        double persentase = parseResponse(response);
        String kategori = tentukanKategori(data.getSistolik(), data.getDiastolik());
        String saran = generateSaran(kategori);

        return new HasilPrediksi(persentase, kategori, saran);
    }

    private String kirimKeApi(String jsonData) {
        // sementara masih dummy
        return "0.78";
    }

    public double parseResponse(String response) {
        return Double.parseDouble(response) * 100;
    }

    public String tentukanKategori(int sistolik, int diastolik) {
        if (sistolik < 120 && diastolik < 80) {
            return "Normal";
        } else if (sistolik >= 120 && sistolik <= 129 && diastolik < 80) {
            return "Elevated";
        } else if ((sistolik >= 130 && sistolik <= 139) || (diastolik >= 80 && diastolik <= 89)) {
            return "Hipertensi Tahap 1";
        } else {
            return "Hipertensi Tahap 2";
        }
    }

    public String generateSaran(String kategoriRisiko) {
        switch (kategoriRisiko) {
            case "Normal":
                return "Pertahankan pola hidup sehat.";
            case "Elevated":
                return "Kurangi garam dan rutin olahraga.";
            case "Hipertensi Tahap 1":
                return "Perbaiki pola makan, rutin cek tekanan darah, dan mulai pola hidup sehat.";
            case "Hipertensi Tahap 2":
                return "Segera konsultasi ke dokter dan lakukan pemeriksaan lebih lanjut.";
            default:
                return "Lakukan pemeriksaan lebih lanjut.";
        }
    }

    public void simpanHasil(HasilPrediksi hasil, RiwayatPrediksi riwayat) {
        riwayat.tambahHasil(hasil);
    }
}
