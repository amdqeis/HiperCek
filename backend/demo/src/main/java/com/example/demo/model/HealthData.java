package com.example.demo.model;

import java.util.Map;

public class HealthData {
    private int usia;
    private int sistolik;
    private int diastolik;
    private double bmi;
    private boolean riwayatKeluarga;
    private String merokok;
    private int aktivitasFisik;
    private boolean diabetes;

    // DIPERBOLEHKAN MENAMBAH Constructor tambahan (no-arg)
    public HealthData() {}

    public HealthData(
        int usia,
        int sistolik,
        int diastolik,
        double bmi,
        boolean riwayatKeluarga,
        String merokok,
        int aktivitasFisik,
        boolean diabetes
    ) {
        this.usia = usia;
        this.sistolik = sistolik;
        this.diastolik = diastolik;
        this.bmi = bmi;
        this.riwayatKeluarga = riwayatKeluarga;
        this.merokok = merokok;
        this.aktivitasFisik = aktivitasFisik;
        this.diabetes = diabetes;
    }

    public int getUsia() { return usia; }
    public void setUsia(int usia) { this.usia = usia; }

    public int getSistolik() { return sistolik; }
    public void setSistolik(int sistolik) { this.sistolik = sistolik; }

    public int getDiastolik() { return diastolik; }
    public void setDiastolik(int diastolik) { this.diastolik = diastolik; }

    public double getBmi() { return bmi; }
    public void setBmi(double bmi) { this.bmi = bmi; }

    public boolean isRiwayatKeluarga() { return riwayatKeluarga; }
    public void setRiwayatKeluarga(boolean riwayatKeluarga) { this.riwayatKeluarga = riwayatKeluarga; }

    public String getMerokok() { return merokok; }
    public void setMerokok(String merokok) { this.merokok = merokok; }

    public int getAktivitasFisik() { return aktivitasFisik; }
    public void setAktivitasFisik(int aktivitasFisik) { this.aktivitasFisik = aktivitasFisik; }

    public boolean isDiabetes() { return diabetes; }
    public void setDiabetes(boolean diabetes) { this.diabetes = diabetes; }

    public boolean isValid() {
        if (usia < 1 || usia > 120) return false;
        if (sistolik < 70 || sistolik > 250) return false;
        if (diastolik < 40 || diastolik > 150 || diastolik >= sistolik) return false;
        if (bmi < 10.0 || bmi > 70.0) return false;
        if (merokok == null || (!merokok.equals("Never") && !merokok.equals("Former") && !merokok.equals("Current"))) return false;
        if (aktivitasFisik < 0 || aktivitasFisik > 2) return false;
        return true;
    }

    public String getPhysicalActivityLevel() {
        return switch (aktivitasFisik) {
            case 0 -> "Low";
            case 1 -> "Moderate";
            case 2 -> "High";
            default -> throw new IllegalStateException("Aktivitas fisik di luar rentang.");
        };
    }

    public Map<String, Object> toHypertensionLevel() {
        return Map.of(
            "age", usia,
            "bmi", bmi,
            "systolic_bp", sistolik,
            "diastolic_bp", diastolik,
            "family_history", riwayatKeluarga ? 1 : 0,
            "smoking_status", merokok,
            "physical_activity_level", getPhysicalActivityLevel(),
            "diabetes", diabetes ? 1 : 0
        );
    }

    public Map<String, Object> toCardiovascularLevel() {
        return Map.of(
            "age", usia,
            "bmi", bmi,
            "systolic_bp", sistolik,
            "diastolic_bp", diastolik,
            "smoking_status", merokok,
            "physical_activity_level", getPhysicalActivityLevel()
        );
    }
}
