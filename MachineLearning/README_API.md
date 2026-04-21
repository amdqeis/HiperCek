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
