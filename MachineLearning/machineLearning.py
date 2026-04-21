# =========================
# 1) Install & Import
# =========================

import os
import glob
import json
import tempfile
import warnings
warnings.filterwarnings("ignore")

import pandas as pd
import joblib

from sklearn.model_selection import train_test_split
from sklearn.compose import ColumnTransformer
from sklearn.pipeline import Pipeline
from sklearn.preprocessing import OneHotEncoder, StandardScaler
from sklearn.impute import SimpleImputer
from sklearn.metrics import (
    accuracy_score,
    precision_score,
    recall_score,
    f1_score,
    roc_auc_score,
    confusion_matrix,
    classification_report
)
from sklearn.linear_model import LogisticRegression
from sklearn.ensemble import RandomForestClassifier

# =========================
# 2) Load dataset
# =========================
DATA_DIR = "/home/key/.cache/kagglehub/datasets/ankushpanday1/hypertension-risk-prediction-dataset/versions/1"
TRAIN_COUNTRY = os.getenv("TRAIN_COUNTRY", "Indonesia").strip()
MAX_MODEL_SIZE_MB = float(os.getenv("MAX_MODEL_SIZE_MB", "100"))
MODEL_SIZE_LIMIT_BYTES = int(MAX_MODEL_SIZE_MB * 1024 * 1024)
MODEL_COMPRESSION_LEVEL = int(os.getenv("MODEL_COMPRESSION_LEVEL", "3"))

csv_files = glob.glob(os.path.join(DATA_DIR, "**", "*.csv"), recursive=True)

if not csv_files:
    raise FileNotFoundError(f"Tidak ada file CSV di folder: {DATA_DIR}")

print("File CSV ditemukan:")
for f in csv_files:
    print("-", f)

df = pd.read_csv(csv_files[0])
print("\nUkuran dataset:", df.shape)
print(df.head())

def estimate_model_size_bytes(model) -> int:
    with tempfile.NamedTemporaryFile(suffix=".joblib") as temp_file:
        joblib.dump(model, temp_file.name, compress=MODEL_COMPRESSION_LEVEL)
        return os.path.getsize(temp_file.name)

# =========================
# 3) Standarisasi nama kolom
# =========================
df.columns = [c.strip().lower().replace(" ", "_") for c in df.columns]

# perbaiki typo / variasi nama kolom
rename_map = {
    "dystolic_bp": "diastolic_bp",
    "diastolicbp": "diastolic_bp",
    "systolicbp": "systolic_bp",
    "hypertension": "hypertension",
    "family_history": "family_history",
    "smoking_status": "smoking_status",
    "physical_activity_level": "physical_activity_level",
    "diabetes": "diabetes",
    "age": "age",
    "bmi": "bmi"
}
df = df.rename(columns=rename_map)

print("\nNama kolom setelah normalisasi:")
print(df.columns.tolist())

# =========================
# 3a) Filter dataset agar model tetap ringan
# =========================
applied_country_filter = None
country_col = "country" if "country" in df.columns else None

if country_col and TRAIN_COUNTRY:
    country_mask = df[country_col].astype(str).str.strip().str.lower() == TRAIN_COUNTRY.lower()
    filtered_df = df.loc[country_mask].copy()

    if filtered_df.empty:
        print(
            f"\nCountry '{TRAIN_COUNTRY}' tidak ditemukan di dataset. "
            "Training tetap memakai seluruh data."
        )
    else:
        applied_country_filter = TRAIN_COUNTRY
        print(
            f"\nDataset difilter ke Country='{TRAIN_COUNTRY}': "
            f"{len(filtered_df)} dari {len(df)} baris."
        )
        df = filtered_df
elif TRAIN_COUNTRY:
    print("\nKolom country tidak ditemukan. Training memakai seluruh data.")

# =========================
# 4) Tentukan target dan fitur
# =========================
target_col = None
for cand in ["hypertension", "target", "label", "outcome"]:
    if cand in df.columns:
        target_col = cand
        break

if target_col is None:
    raise ValueError("Kolom target tidak ditemukan. Pastikan ada kolom Hypertension / target / label.")

required_features = [
    "age",
    "bmi",
    "systolic_bp",
    "diastolic_bp",
    "family_history",
    "smoking_status",
    "physical_activity_level",
    "diabetes"
]

missing_features = [c for c in required_features if c not in df.columns]
if missing_features:
    raise ValueError(f"Kolom fitur berikut tidak ditemukan: {missing_features}")

# =========================
# 5) Bersihkan target
#    High = 1, Low = 0
# =========================
def encode_target(series):
    s = series.astype(str).str.strip().str.lower()
    mapped = s.map({
        "high": 1,
        "low": 0,
        "yes": 1,
        "no": 0,
        "1": 1,
        "0": 0,
        "true": 1,
        "false": 0
    })
    if mapped.isna().any():
        raise ValueError(
            f"Target column '{target_col}' punya label yang belum dikenali. "
            f"Contoh nilai unik: {series.dropna().unique()[:10]}"
        )
    return mapped.astype(int)

y = encode_target(df[target_col])

if y.nunique() < 2:
    raise ValueError("Target hanya memiliki satu kelas setelah filtering. Training tidak bisa dilanjutkan.")

# =========================
# 6) Bersihkan fitur binary
# =========================
binary_map = {
    "yes": 1, "no": 0,
    "y": 1, "n": 0,
    "1": 1, "0": 0,
    "true": 1, "false": 0
}

for col in ["family_history", "diabetes"]:
    df[col] = df[col].astype(str).str.strip().str.lower().map(binary_map)

# =========================
# 7) Siapkan X
# =========================
X = df[required_features].copy()

numeric_features = ["age", "bmi", "systolic_bp", "diastolic_bp", "family_history", "diabetes"]
categorical_features = ["smoking_status", "physical_activity_level"]

# =========================
# 8) Preprocessing pipeline
# =========================
numeric_transformer = Pipeline(steps=[
    ("imputer", SimpleImputer(strategy="median")),
    ("scaler", StandardScaler())
])

categorical_transformer = Pipeline(steps=[
    ("imputer", SimpleImputer(strategy="most_frequent")),
    ("onehot", OneHotEncoder(handle_unknown="ignore"))
])

preprocessor = ColumnTransformer(
    transformers=[
        ("num", numeric_transformer, numeric_features),
        ("cat", categorical_transformer, categorical_features)
    ]
)

# =========================
# 9) Split data
# =========================
X_train, X_test, y_train, y_test = train_test_split(
    X, y,
    test_size=0.2,
    random_state=42,
    stratify=y
)

print("Train:", X_train.shape, "Test:", X_test.shape)

# =========================
# 10) Coba beberapa model
# =========================
models = {
    "Logistic Regression": LogisticRegression(
        max_iter=3000,
        class_weight="balanced",
        random_state=42,
        solver="liblinear"
    ),
    "Random Forest": RandomForestClassifier(
        n_estimators=120,
        max_depth=12,
        min_samples_leaf=5,
        min_samples_split=20,
        random_state=42,
        class_weight="balanced_subsample",
        n_jobs=-1
    )
}

results = []
best_candidate = None
fallback_candidate = None

for name, model in models.items():
    pipe = Pipeline(steps=[
        ("preprocess", preprocessor),
        ("model", model)
    ])

    pipe.fit(X_train, y_train)
    y_pred = pipe.predict(X_test)
    y_prob = pipe.predict_proba(X_test)[:, 1]

    acc = accuracy_score(y_test, y_pred)
    prec = precision_score(y_test, y_pred, zero_division=0)
    rec = recall_score(y_test, y_pred, zero_division=0)
    f1 = f1_score(y_test, y_pred, zero_division=0)
    auc = roc_auc_score(y_test, y_prob)
    model_size_bytes = estimate_model_size_bytes(pipe)
    model_size_mb = model_size_bytes / (1024 * 1024)
    within_size_limit = model_size_bytes <= MODEL_SIZE_LIMIT_BYTES

    results.append({
        "Model": name,
        "Accuracy": acc,
        "Precision": prec,
        "Recall": rec,
        "F1": f1,
        "ROC_AUC": auc,
        "Model_Size_MB": round(model_size_mb, 2),
        "Within_Size_Limit": within_size_limit
    })

    print("\n" + "="*60)
    print(f"MODEL: {name}")
    print("="*60)
    print("Confusion Matrix:")
    print(confusion_matrix(y_test, y_pred))
    print("\nClassification Report:")
    print(classification_report(y_test, y_pred, target_names=["Low", "High"]))
    print(f"Ukuran model terserialisasi: {model_size_mb:.2f} MB")
    print(f"Batas ukuran model: {MAX_MODEL_SIZE_MB:.2f} MB")

    candidate = {
        "name": name,
        "model": pipe,
        "f1": f1,
        "size_bytes": model_size_bytes,
        "size_mb": model_size_mb,
        "within_size_limit": within_size_limit
    }

    if fallback_candidate is None:
        fallback_candidate = candidate
    else:
        fallback_is_better = (
            candidate["size_bytes"] < fallback_candidate["size_bytes"]
            or (
                candidate["size_bytes"] == fallback_candidate["size_bytes"]
                and candidate["f1"] > fallback_candidate["f1"]
            )
        )
        if fallback_is_better:
            fallback_candidate = candidate

    if within_size_limit:
        if best_candidate is None:
            best_candidate = candidate
        else:
            candidate_is_better = (
                candidate["f1"] > best_candidate["f1"]
                or (
                    candidate["f1"] == best_candidate["f1"]
                    and candidate["size_bytes"] < best_candidate["size_bytes"]
                )
            )
            if candidate_is_better:
                best_candidate = candidate

if best_candidate is None:
    best_candidate = fallback_candidate
    print(
        "\nTidak ada model yang berada di bawah batas ukuran. "
        "Model terkecil dipilih sebagai fallback."
    )

best_model = best_candidate["model"]
best_name = best_candidate["name"]
best_f1 = best_candidate["f1"]
best_model_size_mb = best_candidate["size_mb"]

results_df = pd.DataFrame(results).sort_values(
    by=["Within_Size_Limit", "F1", "Model_Size_MB"],
    ascending=[False, False, True]
)
print("\nHasil perbandingan model:")
print(results_df)

print(f"\nModel terpilih dengan mempertimbangkan F1-score dan batas ukuran: {best_name} (F1 = {best_f1:.4f})")
print(f"Ukuran model terpilih: {best_model_size_mb:.2f} MB")

# =========================
# 11) Simpan model terbaik
# =========================
MODEL_PATH = "hypertension_model.joblib"
joblib.dump(best_model, MODEL_PATH, compress=MODEL_COMPRESSION_LEVEL)
print(f"\nModel disimpan ke: {MODEL_PATH}")

# Simpan info kolom juga
INFO_PATH = "hypertension_model_info.json"
model_info = {
    "features": required_features,
    "numeric_features": numeric_features,
    "categorical_features": categorical_features,
    "target": target_col,
    "best_model": best_name,
    "training_country": applied_country_filter,
    "dataset_rows": int(len(df)),
    "max_model_size_mb": MAX_MODEL_SIZE_MB,
    "model_size_mb": round(best_model_size_mb, 2)
}
with open(INFO_PATH, "w") as f:
    json.dump(model_info, f, indent=2)

print(f"Info model disimpan ke: {INFO_PATH}")

# =========================
# 12) Fungsi prediksi risiko (%)
# =========================
def predict_hypertension_risk(
    model,
    age,
    bmi,
    systolic_bp,
    diastolic_bp,
    family_history,
    smoking_status,
    physical_activity_level,
    diabetes
):
    sample = pd.DataFrame([{
        "age": age,
        "bmi": bmi,
        "systolic_bp": systolic_bp,
        "diastolic_bp": diastolic_bp,
        "family_history": 1 if str(family_history).strip().lower() in ["yes", "y", "1", "true"] else 0,
        "smoking_status": smoking_status,
        "physical_activity_level": physical_activity_level,
        "diabetes": 1 if str(diabetes).strip().lower() in ["yes", "y", "1", "true"] else 0
    }])

    risk_prob = model.predict_proba(sample)[0, 1] * 100
    predicted_label = "High" if risk_prob >= 50 else "Low"
    return risk_prob, predicted_label

# Contoh penggunaan
risk, label = predict_hypertension_risk(
    best_model,
    age=25,
    bmi=24.5,
    systolic_bp=130,
    diastolic_bp=85,
    family_history="No",
    smoking_status="Never",
    physical_activity_level="Moderate",
    diabetes="No"
)

print(f"\nContoh prediksi:")
print(f"Risiko hipertensi: {risk:.2f}%")
print(f"Prediksi label: {label}")
