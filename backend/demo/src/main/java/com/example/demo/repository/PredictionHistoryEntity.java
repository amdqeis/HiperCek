package com.example.demo.repository;

import com.example.demo.model.HasilPrediksi;
import com.example.demo.model.HealthData;
import com.example.demo.model.RiwayatPrediksi;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;

/**
 * JPA entity untuk tabel prediction_history di MySQL.
 * Menyimpan semua field secara flat (bukan relasi) agar query sederhana.
 *
 * Konversi domain <-> entity:
 *   fromDomain(RiwayatPrediksi) → entity untuk disimpan ke DB
 *   toDomain()                  → rebuild RiwayatPrediksi dari DB
 */
@Entity
@Table(name = "prediction_history")
public class PredictionHistoryEntity {

    @Id
    private String id;

    private LocalDateTime waktuTambah;

    // === Data Kesehatan (dari HealthData) ===
    private int usia;
    private int sistolik;
    private int diastolik;
    private double bmi;
    private boolean riwayatKeluarga;
    private String merokok;
    private int aktivitasFisik;
    private boolean diabetes;

    // === Hasil Prediksi Hipertensi ===
    private double persentaseHipertensi;
    private String kategoriHipertensi;
    private String catatanHipertensi;

    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = "TEXT")
    private List<String> saranHipertensi;

    // === Hasil Prediksi Kardiovaskular ===
    private double persentaseKardiovaskular;
    private String kategoriKardiovaskular;
    private String catatanKardiovaskular;

    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = "TEXT")
    private List<String> saranKardiovaskular;

    protected PredictionHistoryEntity() {}

    /**
     * Konversi dari domain model ke entity untuk disimpan ke database.
     */
    public static PredictionHistoryEntity fromDomain(RiwayatPrediksi domain) {
        PredictionHistoryEntity entity = new PredictionHistoryEntity();
        entity.id = domain.getId();
        entity.waktuTambah = domain.getWaktuTambah();

        // Salin data kesehatan
        HealthData hd = domain.getHealthData();
        entity.usia = hd.getUsia();
        entity.sistolik = hd.getSistolik();
        entity.diastolik = hd.getDiastolik();
        entity.bmi = hd.getBmi();
        entity.riwayatKeluarga = hd.isRiwayatKeluarga();
        entity.merokok = hd.getMerokok();
        entity.aktivitasFisik = hd.getAktivitasFisik();
        entity.diabetes = hd.isDiabetes();

        // Salin hasil hipertensi
        HasilPrediksi hp = domain.getHipertensi();
        entity.persentaseHipertensi = hp.getPersentaseRisiko();
        entity.kategoriHipertensi = hp.getKategoriRisiko();
        entity.catatanHipertensi = hp.getCatatan();
        entity.saranHipertensi = hp.getSaran();

        // Salin hasil kardiovaskular
        HasilPrediksi cv = domain.getKardiovaskular();
        entity.persentaseKardiovaskular = cv.getPersentaseRisiko();
        entity.kategoriKardiovaskular = cv.getKategoriRisiko();
        entity.catatanKardiovaskular = cv.getCatatan();
        entity.saranKardiovaskular = cv.getSaran();

        return entity;
    }

    /**
     * Konversi dari entity database kembali ke domain model.
     */
    public RiwayatPrediksi toDomain() {
        HealthData healthData = new HealthData(
            usia, sistolik, diastolik, bmi,
            riwayatKeluarga, merokok, aktivitasFisik, diabetes
        );

        HasilPrediksi hipertensi = new HasilPrediksi(
            persentaseHipertensi, kategoriHipertensi, saranHipertensi
        );
        hipertensi.setCatatan(catatanHipertensi);

        HasilPrediksi kardiovaskular = new HasilPrediksi(
            persentaseKardiovaskular, kategoriKardiovaskular, saranKardiovaskular
        );
        kardiovaskular.setCatatan(catatanKardiovaskular);

        return new RiwayatPrediksi(id, waktuTambah, healthData, hipertensi, kardiovaskular);
    }
}
