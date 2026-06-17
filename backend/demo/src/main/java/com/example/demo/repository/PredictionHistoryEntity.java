package com.example.demo.repository;

import java.time.LocalDateTime;
import java.util.List;

import com.example.demo.model.HasilPrediksi;
import com.example.demo.model.HealthData;
import com.example.demo.model.RiwayatPrediksi;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "riwayat_prediksi")
public class PredictionHistoryEntity {
    @Id
    private String id;

    @Column(name = "waktu_tambah", nullable = false)
    private LocalDateTime waktuTambah;

    @Column(name = "usia")
    private Integer usia;

    @Column(name = "bmi")
    private Double bmi;

    @Column(name = "sistolik")
    private Integer sistolik;

    @Column(name = "diastolik")
    private Integer diastolik;

    @Column(name = "riwayat_keluarga")
    private Boolean riwayatKeluarga;

    @Column(name = "status_merokok")
    private String statusMerokok;

    @Column(name = "aktivitas_fisik")
    private Integer aktivitasFisik;

    @Column(name = "diabetes")
    private Boolean diabetes;

    @Column(name = "hipertensi_persentase_risiko", nullable = false)
    private double hipertensiPersentaseRisiko;

    @Column(name = "hipertensi_kategori_risiko", nullable = false)
    private String hipertensiKategoriRisiko;

    @Column(name = "hipertensi_catatan", nullable = false, length = 1000)
    private String hipertensiCatatan;

    @Convert(converter = StringListConverter.class)
    @Column(name = "hipertensi_saran", nullable = false, length = 2000)
    private List<String> hipertensiSaran;

    @Column(name = "kardiovaskular_persentase_risiko", nullable = false)
    private double kardiovaskularPersentaseRisiko;

    @Column(name = "kardiovaskular_kategori_risiko", nullable = false)
    private String kardiovaskularKategoriRisiko;

    @Column(name = "kardiovaskular_catatan", nullable = false, length = 1000)
    private String kardiovaskularCatatan;

    @Convert(converter = StringListConverter.class)
    @Column(name = "kardiovaskular_saran", nullable = false, length = 2000)
    private List<String> kardiovaskularSaran;

    protected PredictionHistoryEntity() {
    }

    private PredictionHistoryEntity(RiwayatPrediksi item) {
        HasilPrediksi hipertensi = item.getHipertensi();
        HasilPrediksi kardiovaskular = item.getKardiovaskular();
        HealthData healthData = item.getHealthData();

        this.id = item.getId();
        this.waktuTambah = item.getWaktuTambah();

        if (healthData != null) {
            this.usia = healthData.getUsia();
            this.bmi = healthData.getBmi();
            this.sistolik = healthData.getSistolik();
            this.diastolik = healthData.getDiastolik();
            this.riwayatKeluarga = healthData.isRiwayatKeluarga();
            this.statusMerokok = healthData.getMerokok();
            this.aktivitasFisik = healthData.getAktivitasFisik();
            this.diabetes = healthData.isDiabetes();
        }

        this.hipertensiPersentaseRisiko = hipertensi.getPersentaseRisiko();
        this.hipertensiKategoriRisiko = hipertensi.getKategoriRisiko();
        this.hipertensiCatatan = hipertensi.getCatatan();
        this.hipertensiSaran = hipertensi.getSaran();

        this.kardiovaskularPersentaseRisiko = kardiovaskular.getPersentaseRisiko();
        this.kardiovaskularKategoriRisiko = kardiovaskular.getKategoriRisiko();
        this.kardiovaskularCatatan = kardiovaskular.getCatatan();
        this.kardiovaskularSaran = kardiovaskular.getSaran();
    }

    public static PredictionHistoryEntity fromDomain(RiwayatPrediksi item) {
        return new PredictionHistoryEntity(item);
    }

    public RiwayatPrediksi toDomain() {
        HasilPrediksi hipertensi = new HasilPrediksi(
                hipertensiPersentaseRisiko,
                hipertensiKategoriRisiko,
                hipertensiSaran
        );
        hipertensi.setCatatan(hipertensiCatatan);

        HasilPrediksi kardiovaskular = new HasilPrediksi(
                kardiovaskularPersentaseRisiko,
                kardiovaskularKategoriRisiko,
                kardiovaskularSaran
        );
        kardiovaskular.setCatatan(kardiovaskularCatatan);

        HealthData healthData = null;
        if (usia != null && bmi != null && sistolik != null && diastolik != null) {
            healthData = new HealthData(
                    usia,
                    sistolik,
                    diastolik,
                    bmi,
                    Boolean.TRUE.equals(riwayatKeluarga),
                    statusMerokok,
                    aktivitasFisik != null ? aktivitasFisik : 0,
                    Boolean.TRUE.equals(diabetes)
            );
        }

        return new RiwayatPrediksi(
                id,
                waktuTambah,
                healthData,
                new HasilPrediksi[]{hipertensi, kardiovaskular}
        );
    }
}