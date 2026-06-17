package com.example.demo.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class RiwayatPrediksi {
    private String id;
    private LocalDateTime waktuTambah;
    private HealthData healthData;
    private HasilPrediksi[] daftarHasil;

    public RiwayatPrediksi(HasilPrediksi[] daftarHasil) {
        this(null, daftarHasil);
    }

    public RiwayatPrediksi(HealthData healthData, HasilPrediksi[] daftarHasil) {
        this.healthData = healthData;
        this.daftarHasil = daftarHasil;
        this.id = UUID.randomUUID().toString();
        this.waktuTambah = LocalDateTime.now();
    }

    public RiwayatPrediksi(String id, LocalDateTime waktuTambah, HasilPrediksi[] daftarHasil) {
        this(id, waktuTambah, null, daftarHasil);
    }

    public RiwayatPrediksi(String id, LocalDateTime waktuTambah, HealthData healthData, HasilPrediksi[] daftarHasil) {
        this.id = id;
        this.waktuTambah = waktuTambah;
        this.healthData = healthData;
        this.daftarHasil = daftarHasil;
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getWaktuTambah() {
        return waktuTambah;
    }

    public HealthData getHealthData() {
        return healthData;
    }

    public HasilPrediksi getHipertensi() {
        if (daftarHasil != null && daftarHasil.length > 0) {
            return daftarHasil[0];
        }
        return null;
    }

    public HasilPrediksi getKardiovaskular() {
        if (daftarHasil != null && daftarHasil.length > 1) {
            return daftarHasil[1];
        }
        return null;
    }
}