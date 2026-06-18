"use client";

import { AppShell } from "@/components/app-shell";
import { RefreshIcon } from "@/components/icons";
import { RiskCard } from "@/components/risk-card";
import { createPrediction } from "@/lib/api";
import type { PredictionFormData, PredictionResponse } from "@/lib/types";
<<<<<<< HEAD
import { useState } from "react";
=======
import { useEffect, useState } from "react";
>>>>>>> origin/Caca

const initialForm: PredictionFormData = {
  age: 45,
  bmi: 27.4,
  systolicBp: 140,
  diastolicBp: 90,
  familyHistory: true,
  smokingStatus: "Former",
  physicalActivityLevel: "Moderate",
  diabetes: false,
};

type FieldErrors = Partial<Record<keyof PredictionFormData, string>>;

function parseFieldErrors(error: unknown): FieldErrors {
  if (error && typeof error === "object" && "fieldErrors" in error) {
    return (error as { fieldErrors: FieldErrors }).fieldErrors;
  }
  return {};
}

export default function PredictPage() {
  const [form, setForm] = useState<PredictionFormData>(initialForm);
  const [fieldErrors, setFieldErrors] = useState<FieldErrors>({});
  const [submitError, setSubmitError] = useState<string | null>(null);
<<<<<<< HEAD
  const [result, setResult] = useState<PredictionResponse | null>(() => {
    if (typeof window === "undefined") {
      return null;
    }

    const stored = window.sessionStorage.getItem("medcheck:last-result");
    if (!stored) {
      return null;
    }

    try {
      return JSON.parse(stored) as PredictionResponse;
    } catch {
      window.sessionStorage.removeItem("medcheck:last-result");
      return null;
    }
  });
  const [isSubmitting, setIsSubmitting] = useState(false);
=======
  const [result, setResult] = useState<PredictionResponse | null>(null);
  const [mounted, setMounted] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  
  useEffect(() => {
    setMounted(true);

    const stored = window.sessionStorage.getItem("medcheck:last-result");
    if (!stored) {
      return;
    }

    try {
      setResult(JSON.parse(stored) as PredictionResponse);
    } catch {
      window.sessionStorage.removeItem("medcheck:last-result");
    }
  }, []);
>>>>>>> origin/Caca

  function updateField<K extends keyof PredictionFormData>(key: K, value: PredictionFormData[K]) {
    setForm((current) => ({ ...current, [key]: value }));
    setFieldErrors((current) => ({ ...current, [key]: undefined }));
  }

  async function handleSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setIsSubmitting(true);
    setSubmitError(null);
    setFieldErrors({});

    try {
      const response = await createPrediction(form);
      setResult(response);
      window.sessionStorage.setItem("medcheck:last-result", JSON.stringify(response));
    } catch (error) {
      setFieldErrors(parseFieldErrors(error));
      setSubmitError(error instanceof Error ? error.message : "Prediksi gagal diproses.");
    } finally {
      setIsSubmitting(false);
    }
  }

  function resetResult() {
    setResult(null);
    setSubmitError(null);
    window.sessionStorage.removeItem("medcheck:last-result");
  }

<<<<<<< HEAD
=======
  if (!mounted) {
    return (
      <AppShell active="predict">
        <section className="mx-auto max-w-[1120px] space-y-8">
          <div className="panel rounded-[2rem] p-8 text-lg text-muted">
            Memuat halaman prediksi...
          </div>
        </section>
      </AppShell>
    );
  }

>>>>>>> origin/Caca
  if (result) {
    return (
      <AppShell active="predict">
        <section className="mx-auto max-w-[1080px] space-y-10">
          <div className="text-center">
            <h1 className="heading-font text-4xl font-bold">Hasil Analisis Kesehatan Terpadu</h1>
            <p className="mt-4 text-lg text-muted">
              Berdasarkan data klinis dan parameter kesehatan Anda.
            </p>
          </div>

          <div className="grid gap-8 xl:grid-cols-2">
            <RiskCard title="Kardiovaskular" risk={result.cardiovascular} />
            <RiskCard title="Hipertensi" risk={result.hypertension} />
          </div>

          <section className="panel rounded-[2.25rem] p-8">
            <h2 className="text-center text-sm uppercase tracking-[0.3em] text-slate-400">
              Saran Pemulihan Terpadu
            </h2>
            <div className="mt-8 grid gap-5 lg:grid-cols-2">
              {result.hypertension.suggestions.concat(result.cardiovascular.suggestions).slice(0, 4).map((item) => (
                <article key={item} className="rounded-[1.5rem] bg-slate-100/90 p-6">
                  <p className="text-lg font-semibold text-slate-900">{item.split(".")[0]}</p>
                  <p className="mt-3 text-base leading-8 text-muted">{item}</p>
                </article>
              ))}
            </div>
          </section>

          <div className="flex justify-center">
            <button className="button-primary min-w-[280px]" type="button" onClick={resetResult}>
              <RefreshIcon className="h-5 w-5" />
              Mulai Lagi
            </button>
          </div>

          <p className="mx-auto max-w-3xl text-center text-lg leading-9 text-muted">
            Penafian: Hasil ini adalah prediksi berbasis algoritma analisis data dan bukan
            merupakan diagnosis medis final. Selalu hubungi dokter Anda untuk interpretasi hasil
            klinis yang akurat.
          </p>
        </section>
      </AppShell>
    );
  }

  return (
    <AppShell active="predict">
      <section className="mx-auto max-w-[1120px] space-y-8">
        <div className="max-w-3xl">
          <h1 className="heading-font text-4xl font-bold tracking-[-0.03em]">Buat Prediksi Kesehatan</h1>
          <p className="mt-4 text-lg leading-8 text-muted">
            Lengkapi data klinis Anda untuk memprediksi risiko hipertensi dan kardiovaskular dalam
            satu proses.
          </p>
        </div>

        <form className="panel rounded-[2.4rem] p-6 md:p-8" onSubmit={handleSubmit}>
          <div className="grid gap-6 md:grid-cols-2">
            <label className="form-field">
              <span className="field-label">Usia</span>
              <input
                className="field-input"
                min={1}
                max={120}
                type="number"
                value={form.age}
                onChange={(event) => updateField("age", Number(event.target.value))}
              />
              {fieldErrors.age ? <span className="field-error">{fieldErrors.age}</span> : null}
            </label>

            <label className="form-field">
              <span className="field-label">BMI</span>
              <input
                className="field-input"
                min={10}
                max={70}
                step="0.1"
                type="number"
                value={form.bmi}
                onChange={(event) => updateField("bmi", Number(event.target.value))}
              />
              {fieldErrors.bmi ? <span className="field-error">{fieldErrors.bmi}</span> : null}
            </label>

            <label className="form-field">
              <span className="field-label">Tekanan Sistolik</span>
              <input
                className="field-input"
                min={70}
                max={250}
                type="number"
                value={form.systolicBp}
                onChange={(event) => updateField("systolicBp", Number(event.target.value))}
              />
              {fieldErrors.systolicBp ? <span className="field-error">{fieldErrors.systolicBp}</span> : null}
            </label>

            <label className="form-field">
              <span className="field-label">Tekanan Diastolik</span>
              <input
                className="field-input"
                min={40}
                max={150}
                type="number"
                value={form.diastolicBp}
                onChange={(event) => updateField("diastolicBp", Number(event.target.value))}
              />
              {fieldErrors.diastolicBp ? <span className="field-error">{fieldErrors.diastolicBp}</span> : null}
            </label>

            <label className="form-field">
              <span className="field-label">Riwayat Keluarga</span>
              <select
                className="field-input"
                value={String(form.familyHistory)}
                onChange={(event) => updateField("familyHistory", event.target.value === "true")}
              >
                <option value="true">Ada</option>
                <option value="false">Tidak Ada</option>
              </select>
              {fieldErrors.familyHistory ? <span className="field-error">{fieldErrors.familyHistory}</span> : null}
            </label>

            <label className="form-field">
              <span className="field-label">Status Merokok</span>
              <select
                className="field-input"
                value={form.smokingStatus}
                onChange={(event) =>
                  updateField("smokingStatus", event.target.value as PredictionFormData["smokingStatus"])
                }
              >
                <option value="Never">Tidak Pernah</option>
                <option value="Former">Pernah</option>
                <option value="Current">Aktif</option>
              </select>
              {fieldErrors.smokingStatus ? <span className="field-error">{fieldErrors.smokingStatus}</span> : null}
            </label>

            <label className="form-field">
              <span className="field-label">Aktivitas Fisik</span>
              <select
                className="field-input"
                value={form.physicalActivityLevel}
                onChange={(event) =>
                  updateField(
                    "physicalActivityLevel",
                    event.target.value as PredictionFormData["physicalActivityLevel"],
                  )
                }
              >
                <option value="Low">Rendah</option>
                <option value="Moderate">Sedang</option>
                <option value="High">Tinggi</option>
              </select>
              {fieldErrors.physicalActivityLevel ? (
                <span className="field-error">{fieldErrors.physicalActivityLevel}</span>
              ) : null}
            </label>

            <label className="form-field">
              <span className="field-label">Diabetes</span>
              <select
                className="field-input"
                value={String(form.diabetes)}
                onChange={(event) => updateField("diabetes", event.target.value === "true")}
              >
                <option value="false">Tidak</option>
                <option value="true">Ya</option>
              </select>
              {fieldErrors.diabetes ? <span className="field-error">{fieldErrors.diabetes}</span> : null}
            </label>
          </div>

          {submitError ? (
            <div className="mt-6 rounded-[1.4rem] border border-red-100 bg-red-50 px-5 py-4 text-sm text-red-700">
              {submitError}
            </div>
          ) : null}

          <div className="mt-8 flex flex-col gap-4 md:flex-row md:items-center">
            <button className="button-primary min-w-[240px]" disabled={isSubmitting} type="submit">
              {isSubmitting ? "Memproses Prediksi..." : "Analisis Sekarang"}
            </button>
          </div>
        </form>
      </section>
    </AppShell>
  );
}
