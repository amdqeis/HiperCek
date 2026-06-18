package com.example.demo.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain model untuk satu catatan riwayat prediksi.
 * Menyimpan data kesehatan beserta hasil prediksi hipertensi dan kardiovaskular.
 */
public class RiwayatPrediksi {
    private final String id;
    private final LocalDateTime waktuTambah;
    private final HealthData healthData;
    private final HasilPrediksi hipertensi;
    private final HasilPrediksi kardiovaskular;

    /**
     * Membuat riwayat baru dengan ID dan timestamp otomatis.
     * Dipanggil saat user menjalankan prediksi baru.
     *
     * @param healthData data kesehatan input
     * @param hasilArray array berisi [0] hipertensi, [1] kardiovaskular
     */
    public RiwayatPrediksi(HealthData healthData, HasilPrediksi[] hasilArray) {
        this.id = UUID.randomUUID().toString();
        this.waktuTambah = LocalDateTime.now();
        this.healthData = healthData;
        this.hipertensi = hasilArray[0];
        this.kardiovaskular = hasilArray[1];
    }

    /**
     * Membuat ulang riwayat dari data yang sudah ada (misal dari database).
     * ID dan timestamp tidak di-generate ulang.
     */
    public RiwayatPrediksi(
        String id,
        LocalDateTime waktuTambah,
        HealthData healthData,
        HasilPrediksi hipertensi,
        HasilPrediksi kardiovaskular
    ) {
        this.id = id;
        this.waktuTambah = waktuTambah;
        this.healthData = healthData;
        this.hipertensi = hipertensi;
        this.kardiovaskular = kardiovaskular;
    }

    public String getId() { return id; }
    public LocalDateTime getWaktuTambah() { return waktuTambah; }
    public HealthData getHealthData() { return healthData; }
    public HasilPrediksi getHipertensi() { return hipertensi; }
    public HasilPrediksi getKardiovaskular() { return kardiovaskular; }
}
