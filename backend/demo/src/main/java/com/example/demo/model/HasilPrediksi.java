package com.example.demo.model;

import java.time.LocalDateTime;

public class HasilPrediksi {
    private double persentaseRisiko;
    private String kategoriRisiko;
    private String saran;

    public HasilPrediksi(double persentaseRisiko, String kategoriRisiko, String saran) {
        setPersentaseRisiko(persentaseRisiko);
        setKategoriRisiko(kategoriRisiko);
        setSaran(saran);
    }

    public double getPersentaseRisiko() {
        return persentaseRisiko;
    }

    public void setPersentaseRisiko(double persentaseRisiko) {
        this.persentaseRisiko = persentaseRisiko;
    }

    public String getKategoriRisiko() {
        return kategoriRisiko;
    }

    public void setKategoriRisiko(String kategoriRisiko) {
        this.kategoriRisiko = kategoriRisiko;
    }

    public String getSaran() {
        return saran;
    }

    public void setSaran(String saran) {
        this.saran = saran;
    }
}
