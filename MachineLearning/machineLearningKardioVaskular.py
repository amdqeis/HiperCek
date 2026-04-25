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
from sklearn.preprocessing import StandardScaler
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
from sklearn.tree import DecisionTreeClassifier
from sklearn.ensemble import (
    AdaBoostClassifier,
    ExtraTreesClassifier,
    GradientBoostingClassifier,
    HistGradientBoostingClassifier,
    RandomForestClassifier
)
from sklearn.naive_bayes import GaussianNB

# =========================
# 2) Load dataset
# =========================
DATA_DIR = "/home/key/.cache/kagglehub/datasets/sulianova/cardiovascular-disease-dataset/versions/1" 
MAX_MODEL_SIZE_MB = float(os.getenv("MAX_MODEL_SIZE_MB", "100"))
MODEL_SIZE_LIMIT_BYTES = int(MAX_MODEL_SIZE_MB * 1024 * 1024)
MODEL_COMPRESSION_LEVEL = int(os.getenv("MODEL_COMPRESSION_LEVEL", "3"))


csv_files = glob.glob(os.path.join(DATA_DIR, "**", "*.csv"), recursive=True)

if not csv_files:
    raise FileNotFoundError(f"Tidak ada file CSV di folder: {DATA_DIR}")

print("File CSV ditemukan:")
for f in csv_files:
    print("-", f)

df = pd.read_csv(csv_files[0], sep=None, engine="python")
print("\nUkuran dataset awal:", df.shape)
print(df.head())

def estimate_model_size_bytes(model) -> int:
    with tempfile.NamedTemporaryFile(suffix=".joblib") as temp_file:
        joblib.dump(model, temp_file.name, compress=MODEL_COMPRESSION_LEVEL)
        return os.path.getsize(temp_file.name)

# =========================
# 3) Standarisasi & Transformasi Fitur
# =========================
# Standarisasi nama kolom menjadi huruf kecil
df.columns = [c.strip().lower() for c in df.columns]

# Mengubah usia dari hari menjadi tahun (dibagi 365.25 untuk hitungan tahun kabisat)
if 'age' in df.columns:
    df['age'] = (df['age'] / 365.25).astype(int)

# Menghitung BMI dari height (asumsi cm) dan weight (asumsi kg)
# Rumus BMI = weight(kg) / (height(m))^2
if 'height' in df.columns and 'weight' in df.columns:
    df['bmi'] = df['weight'] / ((df['height'] / 100) ** 2)
    # Hapus kolom height dan weight karena sudah direpresentasikan oleh BMI
    df.drop(['height', 'weight'], axis=1, inplace=True)

# Perbaiki variasi nama kolom sesuai permintaan
rename_map = {
    "ap_hi": "systolic",
    "ap_lo": "diastolic",
    "cardio": "target"
}
df = df.rename(columns=rename_map)

print("\nNama kolom setelah transformasi & normalisasi:")
print(df.columns.tolist())

# =========================
# 4) Tentukan target dan fitur
# =========================
target_col = None
for cand in ["target", "cardio", "label", "outcome"]:
    if cand in df.columns:
        target_col = cand
        break

if target_col is None:
    raise ValueError("Kolom target tidak ditemukan. Pastikan ada kolom 'cardio' / 'target' di CSV Anda.")

required_features = [
    "age",
    "bmi",
    "systolic",
    "diastolic",
    "smoke",
    "active"
]

missing_features = [c for c in required_features if c not in df.columns]
if missing_features:
    raise ValueError(f"Kolom fitur berikut tidak ditemukan: {missing_features}")

# =========================
# 5) Bersihkan target
#    Cardio: 1 = Risk, 0 = No Risk
# =========================
y = df[target_col].astype(int)

if y.nunique() < 2:
    raise ValueError("Target hanya memiliki satu kelas. Training tidak bisa dilanjutkan.")

# =========================
# 6) Bersihkan fitur binary
# =========================
binary_map = {
    "yes": 1, "no": 0,
    "y": 1, "n": 0,
    "1": 1, "0": 0,
    "true": 1, "false": 0
}

for col in ["smoke", "active"]:
    if df[col].dtype == "object":
        df[col] = df[col].astype(str).str.strip().str.lower().map(binary_map)
    df[col] = pd.to_numeric(df[col], errors="coerce")

# =========================
# 7) Siapkan X
# =========================
X = df[required_features].copy()

# Semua fitur ini dapat diperlakukan sebagai numerik (smoke dan active sudah 0/1)
numeric_features = ["age", "bmi", "systolic", "diastolic", "smoke", "active"]

# =========================
# 8) Preprocessing pipeline
# =========================
numeric_transformer = Pipeline(steps=[
    ("imputer", SimpleImputer(strategy="median")),
    ("scaler", StandardScaler())
])

preprocessor = ColumnTransformer(
    transformers=[
        ("num", numeric_transformer, numeric_features)
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

print("\nTrain:", X_train.shape, "Test:", X_test.shape)

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
    "Decision Tree": DecisionTreeClassifier(
        max_depth=12,
        min_samples_leaf=10,
        min_samples_split=30,
        class_weight="balanced",
        random_state=42
    ),
    "Random Forest": RandomForestClassifier(
        n_estimators=120,
        max_depth=12,
        min_samples_leaf=5,
        min_samples_split=20,
        random_state=42,
        class_weight="balanced_subsample",
        n_jobs=-1
    ),
    "Extra Trees": ExtraTreesClassifier(
        n_estimators=160,
        max_depth=14,
        min_samples_leaf=5,
        min_samples_split=20,
        random_state=42,
        class_weight="balanced",
        n_jobs=-1
    ),
    "Gradient Boosting": GradientBoostingClassifier(
        n_estimators=160,
        learning_rate=0.05,
        max_depth=3,
        random_state=42
    ),
    "Hist Gradient Boosting": HistGradientBoostingClassifier(
        max_iter=220,
        learning_rate=0.05,
        max_leaf_nodes=31,
        l2_regularization=0.1,
        class_weight="balanced",
        random_state=42
    ),
    "AdaBoost": AdaBoostClassifier(
        n_estimators=160,
        learning_rate=0.05,
        random_state=42
    ),
    "Gaussian Naive Bayes": GaussianNB(
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
    print(classification_report(y_test, y_pred, target_names=["No Risk (0)", "Risk (1)"]))
    print(f"Ukuran model terserialisasi: {model_size_mb:.2f} MB")

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
    print("\nTidak ada model yang berada di bawah batas ukuran. Model terkecil dipilih sebagai fallback.")

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

print(f"\nModel terpilih: {best_name} (F1 = {best_f1:.4f})")
print(f"Ukuran model terpilih: {best_model_size_mb:.2f} MB")

# =========================
# 11) Simpan model terbaik
# =========================
MODEL_PATH = "cardiovascular_model.joblib"
joblib.dump(best_model, MODEL_PATH, compress=MODEL_COMPRESSION_LEVEL)
print(f"\nModel disimpan ke: {MODEL_PATH}")

INFO_PATH = "cardiovascular_model_info.json"
model_info = {
    "features": required_features,
    "numeric_features": numeric_features,
    "target": target_col,
    "best_model": best_name,
    "selection_metric": "F1-score, then smaller serialized model size",
    "model_results": results_df.to_dict(orient="records"),
    "dataset_rows": int(len(df)),
    "max_model_size_mb": MAX_MODEL_SIZE_MB,
    "model_size_mb": round(best_model_size_mb, 2)
}
with open(INFO_PATH, "w", encoding="utf-8") as f:
    json.dump(model_info, f, indent=2)

print(f"Info model disimpan ke: {INFO_PATH}")

# =========================
# 12) Fungsi prediksi risiko (%)
# =========================
def predict_cardiovascular_risk(model, age_years, bmi, systolic, diastolic, smoke, active):
    """
    Fungsi untuk memprediksi probabilitas risiko penyakit kardiovaskular.
    Pastikan `age` sudah dalam bentuk tahun.
    `smoke`: 1 (perokok) atau 0 (bukan perokok).
    `active`: 1 (aktif secara fisik) atau 0 (tidak aktif).
    """
    sample = pd.DataFrame([{
        "age": age_years,
        "bmi": bmi,
        "systolic": systolic,
        "diastolic": diastolic,
        "smoke": int(smoke),
        "active": int(active)
    }])

    risk_prob = model.predict_proba(sample)[0, 1] * 100
    predicted_label = "Risk (1)" if risk_prob >= 50 else "No Risk (0)"
    return risk_prob, predicted_label

# Contoh penggunaan
risk, label = predict_cardiovascular_risk(
    best_model,
    age_years=50,
    bmi=26.5,
    systolic=140,
    diastolic=90,
    smoke=1,
    active=1
)

print("\nHasil akhir contoh prediksi pasien baru:")
print(f"Probabilitas risiko penyakit kardiovaskular: {risk:.2f}%")
print(f"Prediksi kelas: {label}")
