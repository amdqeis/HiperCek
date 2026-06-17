package com.example.demo.service;

import com.example.demo.model.HealthData;
import com.example.demo.model.HasilPrediksi;
import com.example.demo.model.RiwayatPrediksi;
import java.util.List;
import java.util.ArrayList;

public class CardiovascularAIService extends MachineLearningService {

    public CardiovascularAIService(String apiUrl) {
        super(apiUrl);
    }

    @Override
    public HasilPrediksi predict(HealthData data) {
        double risk = 35.0; 
        String category = tentukanKategori(data.getSistolik(), data.getDiastolik());
        List<String> saranList = new ArrayList<>();
        saranList.add(generateSaran(category));
        
        return new HasilPrediksi(risk, category, saranList);
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
        if ("high".equals(kategoriRisiko)) return "Risiko kardiovaskular tinggi.";
        if ("medium".equals(kategoriRisiko)) return "Risiko moderat, perbaiki pola makan.";
        return "Risiko rendah, pertahankan aktivitas fisik.";
    }

    public void simpanHasil(HasilPrediksi hasil, RiwayatPrediksi riwayat) {
        // Implementasi logika penyimpanan
    }
}
