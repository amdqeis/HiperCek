# FastAPI Hypertension Risk API

API ini menyediakan **1 endpoint** untuk menghitung persentase risiko hipertensi berdasarkan file model `hypertension_model.joblib`.

## Endpoint

- Method: `POST`
- URL: `/predict`
- Output: `float` berisi persentase risiko hipertensi, misalnya `72.413598271`

## Format Input

Body request harus berupa JSON dengan field berikut:

```json
{
  "age": 45,
  "bmi": 27.4,
  "systolic_bp": 140,
  "diastolic_bp": 90,
  "family_history": 1,
  "smoking_status": "Former",
  "physical_activity_level": "Moderate",
  "diabetes": 0
}
```

Keterangan field:

- `age`: usia pasien
- `bmi`: body mass index
- `systolic_bp`: tekanan darah sistolik
- `diastolic_bp`: tekanan darah diastolik
- `family_history`: `1` jika punya riwayat keluarga hipertensi, `0` jika tidak
- `smoking_status`: salah satu dari `Current`, `Former`, `Never`
- `physical_activity_level`: salah satu dari `High`, `Low`, `Moderate`
- `diabetes`: `1` jika pasien diabetes, `0` jika tidak

## Menjalankan API

Install dependency:

```bash
./venv/bin/pip install fastapi uvicorn
```

Jalankan server:

```bash
./venv/bin/uvicorn api:app --reload
```

Server akan berjalan di:

```text
http://127.0.0.1:8000
```

Dokumentasi interaktif FastAPI tersedia di:

```text
http://127.0.0.1:8000/docs
```

## Deploy ke VPS dengan PM2 + Domain + SSL

File deploy yang sudah disiapkan di folder `MachineLearning`:

- `ecosystem.config.cjs`
- `deploy_to_vps.sh`
- `deploy/setup_vps.sh`
- `deploy/nginx-machinelearning.conf.template`

### Prasyarat

- Domain sudah diarahkan ke IP VPS lewat DNS record `A`
- User SSH di VPS punya akses `sudo`
- Port `80` dan `443` terbuka di firewall VPS

### Opsi 1: Deploy otomatis dari folder `MachineLearning`

```bash
cd MachineLearning
chmod +x deploy_to_vps.sh

./deploy_to_vps.sh \
  --host 1.2.3.4 \
  --user ubuntu \
  --domain api.contoh.com \
  --email admin@contoh.com
```

Script ini akan:

- upload isi folder `MachineLearning` saja ke VPS
- install dependency Python
- install `nginx`, `certbot`, `nodejs`, dan `pm2`
- menjalankan `api.py` lewat `uvicorn` di `pm2`
- membuat reverse proxy `nginx`
- memasang sertifikat SSL Let's Encrypt

### Opsi 2: Jika folder sudah ada di VPS

Masuk ke VPS lalu jalankan:

```bash
cd /path/ke/folder-fastapi-machinelearning
chmod +x deploy/setup_vps.sh

sudo ./deploy/setup_vps.sh \
  --domain api.contoh.com \
  --email admin@contoh.com \
  --app-user ubuntu
```

### Port dan nama app PM2 kustom

```bash
sudo ./deploy/setup_vps.sh \
  --domain api.contoh.com \
  --email admin@contoh.com \
  --app-user ubuntu \
  --port 8010 \
  --app-name hipercek-ml-prod
```

### Setelah deploy

- Dokumentasi FastAPI: `https://domain-kamu/docs`
- Cek proses PM2: `pm2 list`
- Lihat log PM2: `pm2 logs hipercek-ml-api`

## Contoh Penggunaan

### cURL

```bash
curl -X POST "http://127.0.0.1:8000/predict" \
  -H "Content-Type: application/json" \
  -d '{
    "age": 45,
    "bmi": 27.4,
    "systolic_bp": 140,
    "diastolic_bp": 90,
    "family_history": 1,
    "smoking_status": "Former",
    "physical_activity_level": "Moderate",
    "diabetes": 0
  }'
```

Contoh response:

```json
72.413598271
```

### Python

```python
import requests

payload = {
    "age": 45,
    "bmi": 27.4,
    "systolic_bp": 140,
    "diastolic_bp": 90,
    "family_history": 1,
    "smoking_status": "Former",
    "physical_activity_level": "Moderate",
    "diabetes": 0,
}

response = requests.post("http://127.0.0.1:8000/predict", json=payload, timeout=30)
print(response.json())
```

## Catatan

Urutan dan nama field mengikuti file `hypertension_model_info.json`:

- `age`
- `bmi`
- `systolic_bp`
- `diastolic_bp`
- `family_history`
- `smoking_status`
- `physical_activity_level`
- `diabetes`
