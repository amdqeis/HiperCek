from __future__ import annotations

import json
from pathlib import Path
from typing import Literal

import joblib
import pandas as pd
from fastapi import FastAPI
from pydantic import BaseModel, ConfigDict, Field


BASE_DIR = Path(__file__).resolve().parent
MODEL_PATH = BASE_DIR / "hypertension_model.joblib"
INFO_PATH = BASE_DIR / "hypertension_model_info.json"


def load_model_info() -> dict:
    with INFO_PATH.open("r", encoding="utf-8") as file:
        return json.load(file)


MODEL_INFO = load_model_info()
MODEL = joblib.load(MODEL_PATH)
FEATURE_ORDER = MODEL_INFO["features"]


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


app = FastAPI(
    title="Hypertension Risk API",
    description=(
        "API sederhana untuk memprediksi persentase risiko hipertensi "
        "berdasarkan model machine learning yang sudah dilatih."
    ),
    version="1.0.0",
)


@app.post("/predict", response_model=float, summary="Prediksi persentase risiko hipertensi")
def predict_hypertension_percentage(payload: HypertensionPredictionInput) -> float:
    sample = pd.DataFrame([[getattr(payload, feature) for feature in FEATURE_ORDER]], columns=FEATURE_ORDER)
    probability = MODEL.predict_proba(sample)[0, 1] * 100
    return float(probability)
