# MedCheck Frontend

Frontend ini menggunakan Next.js App Router dan terhubung ke backend Spring Boot untuk:

- membuka landing page,
- mengirim form prediksi kesehatan,
- menampilkan hasil hipertensi dan kardiovaskular,
- melihat serta menghapus history prediksi.

## Menjalankan frontend

Install dependency:

```bash
npm install
```

Jalankan development server:

```bash
npm run dev
```

Frontend akan berjalan di:

```text
http://localhost:3000
```

## Environment

Secara default frontend memanggil backend Spring di:

```text
http://localhost:8080
```

Jika berbeda, set env berikut:

```bash
NEXT_PUBLIC_API_BASE_URL=http://localhost:8080
```

## Route utama

- `/` landing page
- `/predict` form input dan hasil prediksi terakhir
- `/history` daftar riwayat prediksi yang tersimpan di backend
