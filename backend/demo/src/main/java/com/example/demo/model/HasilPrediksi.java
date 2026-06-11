package com.example.demo.model;

import java.util.List;

public class HasilPrediksi {
    private double persentaseRisiko;
    private String kategoriRisiko;
    private String catatan;
    private List<String> saran;

    public HasilPrediksi(
        double persentaseRisiko,
        String kategoriRisiko,
        List<String> saran
    ) {
        this.persentaseRisiko = persentaseRisiko;
        this.kategoriRisiko = kategoriRisiko;
        this.saran = saran != null ? List.copyOf(saran) : null;
    }
    
    // DIPERBOLEHKAN MENAMBAH Setter
    public void setCatatan(String catatan) {
        this.catatan = catatan;
    }

    public double getPersentaseRisiko() {
        return persentaseRisiko;
    }

    public String getKategoriRisiko() {
        return kategoriRisiko;
    }

    public String getCatatan() {
        return catatan;
    }

    public List<String> getSaran() {
        return saran;
    }
}
