package com.example.demo.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class RiwayatPrediksi {
    private final String id;
    private final LocalDateTime waktuTambah;
    private final HasilPrediksi hipertensi;
    private final HasilPrediksi kardiovaskular;

    public RiwayatPrediksi(HasilPrediksi hipertensi, HasilPrediksi kardiovaskular) {
        this.id = UUID.randomUUID().toString();
        this.waktuTambah = LocalDateTime.now();
        this.hipertensi = hipertensi;
        this.kardiovaskular = kardiovaskular;
    }

    public RiwayatPrediksi(
        String id,
        LocalDateTime waktuTambah,
        HasilPrediksi hipertensi,
        HasilPrediksi kardiovaskular
    ) {
        this.id = id;
        this.waktuTambah = waktuTambah;
        this.hipertensi = hipertensi;
        this.kardiovaskular = kardiovaskular;
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getWaktuTambah() {
        return waktuTambah;
    }

    public HasilPrediksi getHipertensi() {
        return hipertensi;
    }

    public HasilPrediksi getKardiovaskular() {
        return kardiovaskular;
    }
}
