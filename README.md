# HiperCek - Health Prediction System

Proyek ini terdiri dari tiga komponen utama: Backend (Spring Boot), Frontend (Next.js), dan Machine Learning API (FastAPI) untuk memprediksi kondisi kesehatan (Hipertensi dan Kardiovaskular).

## Prasyarat (Prerequisites)

Sebelum memulai instalasi, pastikan sistem Anda telah menginstal:
- [Java 17+](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
- [Node.js](https://nodejs.org/en) (versi 18 atau terbaru)
- [Python 3.8+](https://www.python.org/downloads/)
- [MySQL Server](https://dev.mysql.com/downloads/installer/)

## 1. Konfigurasi Database (MySQL)

Backend menggunakan database MySQL. Anda perlu membuat database dan menyesuaikan konfigurasi agar backend bisa terhubung.

1. Buka MySQL server Anda.
2. Buat database baru bernama `pbo` (atau akan otomatis dibuat jika `createDatabaseIfNotExist=true` berfungsi, tetapi disarankan membuat manual):
   ```sql
   CREATE DATABASE pbo;
   ```
3. Konfigurasi kredensial database pada file backend.
   Buka file `backend/demo/src/main/resources/application.properties` dan sesuaikan kredensial Anda:
   ```properties
   app.db.name=pbo
   app.db.username=root
   app.db.password=229229
   ```
   *(Ubah `username` dan `password` sesuai konfigurasi MySQL di komputer Anda)*

## 2. Instalasi dan Menjalankan Machine Learning API (FastAPI)

API Machine Learning menggunakan FastAPI untuk menyediakan endpoint prediksi yang akan diakses oleh backend Java.

1. Buka terminal dan masuk ke direktori `MachineLearning`:
   ```bash
   cd MachineLearning
   ```
2. Buat *virtual environment* (direkomendasikan):
   ```bash
   python3 -m venv venv
   ```
3. Aktifkan *virtual environment*:
   - **Windows:** `venv\Scripts\activate`
   - **Linux/Mac:** `source venv/bin/activate`
4. Instal semua dependensi yang diperlukan:
   ```bash
   pip install -r requirements.txt
   ```
5. Jalankan server FastAPI:
   ```bash
   uvicorn api:app --reload
   ```
   *Server ML API akan berjalan pada `http://127.0.0.1:8000`.*

## 3. Instalasi dan Menjalankan Backend (Spring Boot)

Backend berbasis Java Spring Boot menangani logika bisnis, autentikasi (jika ada), database riwayat, dan meneruskan request prediksi ke ML API.

1. Buka terminal baru dan masuk ke direktori backend:
   ```bash
   cd backend/demo
   ```
2. Jalankan aplikasi menggunakan Maven Wrapper:
   - **Windows:**
     ```bash
     mvnw.cmd spring-boot:run
     ```
   - **Linux/Mac:**
     ```bash
     ./mvnw spring-boot:run
     ```
   *Server Backend akan berjalan pada `http://localhost:8080`.*

## 4. Instalasi dan Menjalankan Frontend (Next.js)

Frontend adalah antarmuka web interaktif yang akan digunakan oleh pengguna.

1. Buka terminal baru dan masuk ke direktori frontend:
   ```bash
   cd frontend
   ```
2. Instal dependensi Node.js:
   ```bash
   npm install
   ```
3. Jalankan development server:
   ```bash
   npm run dev
   ```
   *Buka browser dan akses `http://localhost:3000`.*

---

## 🚀 Menjalankan Semua Service Sekaligus (Linux/Mac)

Jika Anda menggunakan Linux atau macOS, Anda bisa langsung menjalankan ketiga layanan (Backend, Frontend, ML API) secara bersamaan melalui script `start.sh` di root folder:

```bash
# Berikan izin eksekusi (hanya pertama kali)
chmod +x start.sh

# Jalankan script
./start.sh
```

Untuk menghentikan semua service yang berjalan dari script ini, cukup tekan `Ctrl+C` pada terminal tersebut.
