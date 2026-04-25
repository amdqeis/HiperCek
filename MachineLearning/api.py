from __future__ import annotations

import json
from pathlib import Path
from typing import Literal

import joblib
import pandas as pd
from fastapi import FastAPI
from pydantic import BaseModel, ConfigDict, Field


BASE_DIR = Path(__file__).resolve().parent
HYPERTENSION_MODEL_PATH = BASE_DIR / "hypertension_model.joblib"
HYPERTENSION_INFO_PATH = BASE_DIR / "hypertension_model_info.json"
CARDIOVASCULAR_MODEL_PATH = BASE_DIR / "cardiovascular_model.joblib"
CARDIOVASCULAR_INFO_PATH = BASE_DIR / "cardiovascular_model_info.json"


def load_model_info(info_path: Path) -> dict:
    with info_path.open("r", encoding="utf-8") as file:
        return json.load(file)


HYPERTENSION_MODEL_INFO = load_model_info(HYPERTENSION_INFO_PATH)
HYPERTENSION_MODEL = joblib.load(HYPERTENSION_MODEL_PATH)
HYPERTENSION_FEATURE_ORDER = HYPERTENSION_MODEL_INFO["features"]

CARDIOVASCULAR_MODEL_INFO = load_model_info(CARDIOVASCULAR_INFO_PATH)
CARDIOVASCULAR_MODEL = joblib.load(CARDIOVASCULAR_MODEL_PATH)
CARDIOVASCULAR_FEATURE_ORDER = CARDIOVASCULAR_MODEL_INFO["features"]


class HypertensionPredictionInput(BaseModel):
    model_config = ConfigDict(
        json_schema_extra={
            "example": {
                "age": 45,
                "bmi": 27.4,
                "systolic_bp": 140,
                "diastolic_bp": 90,
                "family_history": 1,
                "smoking_status": "Former",
                "physical_activity_level": "Moderate",
                "diabetes": 0,
            }
        }
    )

    age: float = Field(..., gt=0, description="Usia pasien dalam tahun.")
    bmi: float = Field(..., gt=0, description="Body Mass Index.")
    systolic_bp: float = Field(..., gt=0, description="Tekanan darah sistolik.")
    diastolic_bp: float = Field(..., gt=0, description="Tekanan darah diastolik.")
    family_history: Literal[0, 1] = Field(
        ...,
        description="Riwayat keluarga hipertensi. Gunakan 1 untuk ya, 0 untuk tidak.",
    )
    smoking_status: Literal["Current", "Former", "Never"] = Field(
        ...,
        description="Status merokok pasien.",
    )
    physical_activity_level: Literal["High", "Low", "Moderate"] = Field(
        ...,
        description="Tingkat aktivitas fisik pasien.",
    )
    diabetes: Literal[0, 1] = Field(
        ...,
        description="Status diabetes. Gunakan 1 untuk ya, 0 untuk tidak.",
    )


class CardiovascularPredictionInput(BaseModel):
    model_config = ConfigDict(
        json_schema_extra={
            "example": {
                "age": 50,
                "bmi": 26.5,
                "systolic_bp": 140,
                "diastolic_bp": 90,
                "smoking_status": "Never",
                "physical_activity_level": "Low",
            }
        }
    )

    age: float = Field(..., gt=0, description="Usia pasien dalam tahun.")
    bmi: float = Field(..., gt=0, description="Body Mass Index.")
    systolic_bp: float = Field(..., gt=0, description="Tekanan darah sistolik.")
    diastolic_bp: float = Field(..., gt=0, description="Tekanan darah diastolik.")
    smoking_status: str = Field(
        ...,
        description="Status merokok. Never menjadi smoke=0, selain itu smoke=1.",
    )
    physical_activity_level: str = Field(
        ...,
        description="Aktivitas fisik. Low menjadi active=0, selain itu active=1.",
    )


app = FastAPI(
    title="Health Risk API",
    description=(
        "API sederhana untuk memprediksi persentase risiko hipertensi dan "
        "penyakit kardiovaskular berdasarkan model machine learning yang sudah dilatih."
    ),
    version="1.0.0",
)


def normalize_text(value: str) -> str:
    return value.strip().lower().replace("-", "_").replace(" ", "_")


def map_smoking_status_to_smoke(smoking_status: str) -> int:
    return 0 if normalize_text(smoking_status) in {"never", "vener", "tidak_pernah"} else 1


def map_physical_activity_to_active(physical_activity_level: str) -> int:
    return 0 if normalize_text(physical_activity_level) == "low" else 1


@app.post("/predict", response_model=float, summary="Prediksi persentase risiko hipertensi")
def predict_hypertension_percentage(payload: HypertensionPredictionInput) -> float:
    sample = pd.DataFrame(
        [[getattr(payload, feature) for feature in HYPERTENSION_FEATURE_ORDER]],
        columns=HYPERTENSION_FEATURE_ORDER,
    )
    probability = HYPERTENSION_MODEL.predict_proba(sample)[0, 1] * 100
    return float(probability)


@app.post(
    "/predict/cardiovascular",
    response_model=float,
    summary="Prediksi persentase risiko penyakit kardiovaskular",
)
def predict_cardiovascular_percentage(payload: CardiovascularPredictionInput) -> float:
    feature_values = {
        "age": payload.age,
        "bmi": payload.bmi,
        "systolic": payload.systolic_bp,
        "diastolic": payload.diastolic_bp,
        "smoke": map_smoking_status_to_smoke(payload.smoking_status),
        "active": map_physical_activity_to_active(payload.physical_activity_level),
    }
    sample = pd.DataFrame(
        [[feature_values[feature] for feature in CARDIOVASCULAR_FEATURE_ORDER]],
        columns=CARDIOVASCULAR_FEATURE_ORDER,
    )
    probability = CARDIOVASCULAR_MODEL.predict_proba(sample)[0, 1] * 100
    return float(probability)
