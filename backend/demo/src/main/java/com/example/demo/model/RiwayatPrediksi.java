package com.example.demo.model;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class RiwayatPrediksi {

    private ArrayList<HasilPrediksi> daftarHasil;
    private LocalDateTime waktuTambah;

    public RiwayatPrediksi(ArrayList<HasilPrediksi> daftarHasil){
        if (daftarHasil == null){
            this.daftarHasil = new ArrayList<>();
        } else {
            this.daftarHasil = daftarHasil;
        }
        this.WaktuTambah = LocalDateTime.now();
    }

    public void tambahHasil(HasilPrediksi hasil){
        this.daftarHasil.add(hasil);
        this.waktuTambah = LocalDateTime.now();
    }

    public ArrayList<HasilPrediksi>getSemuaHasil(){
        return this.daftarHasil;
    }

    public LocalDateTime getWaktuTambah(){
        return this.waktuTambah;
    }
}
