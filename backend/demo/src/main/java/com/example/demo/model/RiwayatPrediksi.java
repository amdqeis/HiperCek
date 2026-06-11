package com.example.demo.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class RiwayatPrediksi {
    // Tambahan atribut untuk mendukung method getter
    private String id;
    private LocalDateTime waktuTambah;
    
    // Sesuai diagram
    private HasilPrediksi[] daftarHasil;

    public RiwayatPrediksi(HasilPrediksi[] daftarHasil) {
        this.daftarHasil = daftarHasil;
        this.id = UUID.randomUUID().toString();
        this.waktuTambah = LocalDateTime.now();
    }
    
    // DIPERBOLEHKAN MENAMBAH Constructor tambahan
    public RiwayatPrediksi(String id, LocalDateTime waktuTambah, HasilPrediksi[] daftarHasil) {
        this.id = id;
        this.waktuTambah = waktuTambah;
        this.daftarHasil = daftarHasil;
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getWaktuTambah() {
        return waktuTambah;
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
