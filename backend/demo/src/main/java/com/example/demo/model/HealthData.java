package com.example.demo.model;

public class HealthData {
    private int usia;
    private int sistolik;
    private int diastolik;
    private double bmi;
    private boolean riwayatKeluarga;
    private String merokok;
    private int aktivitasFisik;
    private boolean diabetes;

  
    public int getUsia() {
        return usia;
    }

    public void setUsia(int usia) {
        this.usia = usia;
    }

    public int getSistolik() {
        return sistolik;
    }

    public void setSistolik(int sistolik) {
        this.sistolik = sistolik;
    }

    public int getDiastolik() {
        return diastolik;
    }

    public void setDiastolik(int diastolik) {
        this.diastolik = diastolik;
    }

    public double getBmi() {
        return bmi;
    }

    public void setBmi(double bmi) {
        this.bmi = bmi;
    }

    public boolean isRiwayatKeluarga() {
        return riwayatKeluarga;
    }

    public void setRiwayatKeluarga(boolean riwayatKeluarga) {
        this.riwayatKeluarga = riwayatKeluarga;
    }

    public String getMerokok() {
        return merokok;
    }

    public void setMerokok(String merokok) {
        this.merokok = merokok;
    }

    public int getAktivitasFisik() {
        return aktivitasFisik;
    }

    public void setAktivitasFisik(int aktivitasFisik) {
        this.aktivitasFisik = aktivitasFisik;
    }

    public boolean isDiabetes() {
        return diabetes;
    }

    public void setDiabetes(boolean diabetes) {
        this.diabetes = diabetes;
    }

    public boolean isValid() {
        if (usia < 1 || usia > 120) {
            return false;
        }

        // rentang tekanan darah manusia (mmHg)
        if (sistolik < 70 || sistolik > 250) {
            return false;
        }

        // diastolik harus lebih kecil dari sistolik
        if (diastolik < 40 || diastolik > 150 || diastolik >= sistolik) {
            return false;
        }

        if (bmi < 10.0 || bmi > 70.0) {
            return false;
        }

        // status merokok
        if (merokok == null ||
            (!merokok.equals("tidak_pernah") &&
             !merokok.equals("pernah") &&
             !merokok.equals("aktif"))) {
            return false;
        }

        // aktivitas fisik
        if (aktivitasFisik < 0 || aktivitasFisik > 7) {
            return false;
        }

        return true;
    }

  
}
