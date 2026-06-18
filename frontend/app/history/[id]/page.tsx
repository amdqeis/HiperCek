"use client";

import { AppShell } from "@/components/app-shell";
import { getPredictionHistory } from "@/lib/api";
import type { PredictionResponse } from "@/lib/types";
import Link from "next/link";
import { useParams } from "next/navigation";
import { useEffect, useState } from "react";

function categoryLabel(category: PredictionResponse["hypertension"]["category"]) {
  switch (category) {
    case "low":
      return "Risiko Rendah";
    case "medium":
      return "Risiko Sedang";
    default:
      return "Risiko Tinggi";
  }
}

export default function HistoryDetailPage() {
  const params = useParams();
  const id = params.id as string;

  const [item, setItem] = useState<PredictionResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let active = true;

    async function loadDetail() {
      try {
        const history = await getPredictionHistory();
        const selectedItem = history.find((historyItem) => historyItem.id === id);

        if (active) {
          if (selectedItem) {
            setItem(selectedItem);
            setError(null);
          } else {
            setError("Data riwayat tidak ditemukan.");
          }
        }
      } catch (loadError) {
        if (active) {
          setError(loadError instanceof Error ? loadError.message : "Gagal memuat rincian riwayat.");
        }
      } finally {
        if (active) {
          setLoading(false);
        }
      }
    }

    void loadDetail();

    return () => {
      active = false;
    };
  }, [id]);

  return (
    <AppShell active="history">
      <section className="mx-auto max-w-[1080px]">
        <header className="mb-8">
          <h1 className="heading-font text-4xl font-bold tracking-[-0.03em]">
            Rincian Riwayat Prediksi
          </h1>
          <p className="mt-4 text-lg leading-8 text-muted">
            Detail input, output, catatan, dan saran prediksi sebelumnya.
          </p>
        </header>

        {loading ? (
          <div className="panel rounded-[2rem] p-8 text-lg text-muted">
            Memuat rincian riwayat...
          </div>
        ) : null}

        {error ? (
          <div className="panel rounded-[2rem] p-8">
            <p className="text-red-600">{error}</p>

            <Link
              href="/history"
              className="mt-6 inline-flex rounded-2xl bg-[var(--primary)] px-5 py-3 font-semibold !text-white"
            >
              Kembali ke Riwayat
            </Link>
          </div>
        ) : null}

        {!loading && item ? (
          <div className="space-y-6">
            <div className="panel rounded-[2rem] p-6">
              <p className="text-sm uppercase tracking-[0.22em] text-slate-400">
                Waktu Prediksi
              </p>
              <p className="mt-2 text-lg font-semibold text-slate-900">
                {new Date(item.createdAt).toLocaleString("id-ID", {
                  dateStyle: "full",
                  timeStyle: "short",
                })}
              </p>
            </div>

            <div className="panel rounded-[2rem] p-6">
                <p className="text-sm uppercase tracking-[0.22em] text-slate-400">
                    Rincian Data yang Dimasukkan
                </p>

                {item.input ? (
                    <div className="mt-5 grid gap-4 md:grid-cols-2">
                        <div className="rounded-[1.4rem] bg-slate-100/90 p-5">
                            <p className="text-sm text-muted">Usia</p>
                            <p className="mt-2 text-lg font-semibold text-slate-900">
                                {item.input.age} tahun
                            </p>
                        </div>

                        <div className="rounded-[1.4rem] bg-slate-100/90 p-5">
                            <p className="text-sm text-muted">BMI</p>
                            <p className="mt-2 text-lg font-semibold text-slate-900">
                                {item.input.bmi}
                            </p>
                        </div>

                        <div className="rounded-[1.4rem] bg-slate-100/90 p-5">
                            <p className="text-sm text-muted">Tekanan Darah</p>
                            <p className="mt-2 text-lg font-semibold text-slate-900">
                                {item.input.systolicBp}/{item.input.diastolicBp} mmHg
                            </p>
                        </div>

                        <div className="rounded-[1.4rem] bg-slate-100/90 p-5">
                            <p className="text-sm text-muted">Riwayat Keluarga</p>
                            <p className="mt-2 text-lg font-semibold text-slate-900">
                                {item.input.familyHistory ? "Ada" : "Tidak Ada"}
                            </p>
                        </div>

                        <div className="rounded-[1.4rem] bg-slate-100/90 p-5">
                            <p className="text-sm text-muted">Status Merokok</p>
                            <p className="mt-2 text-lg font-semibold text-slate-900">
                                {item.input.smokingStatus === "Never"
                                    ? "Tidak Pernah"
                                    : item.input.smokingStatus === "Former"
                                    ? "Pernah"
                                    : item.input.smokingStatus === "Current"
                                    ? "Saat Ini"
                                    : item.input.smokingStatus}
                            </p>
                        </div>

                        <div className="rounded-[1.4rem] bg-slate-100/90 p-5">
                            <p className="text-sm text-muted">Aktivitas Fisik</p>
                            <p className="mt-2 text-lg font-semibold text-slate-900">
                                {item.input.physicalActivityLevel === "Low" ||
                                item.input.physicalActivityLevel === 1
                                    ? "Rendah"
                                    : item.input.physicalActivityLevel === "Moderate" ||
                                    item.input.physicalActivityLevel === 2
                                    ? "Sedang"
                                    : item.input.physicalActivityLevel === "High" ||
                                    item.input.physicalActivityLevel === 3
                                    ? "Tinggi"
                                    : item.input.physicalActivityLevel}
                            </p>
                        </div>

                        <div className="rounded-[1.4rem] bg-slate-100/90 p-5 md:col-span-2">
                            <p className="text-sm text-muted">Diabetes</p>
                            <p className="mt-2 text-lg font-semibold text-slate-900">
                                {item.input.diabetes ? "Ya" : "Tidak"}
                            </p>
                        </div>
                    </div>
                ) : (
                    <p className="mt-4 text-sm leading-7 text-muted">
                        Detail input belum tersedia untuk riwayat lama. Buat prediksi baru agar data input tersimpan.
                    </p>
                )}
                </div>

            <div className="grid gap-5 md:grid-cols-2">
              <div className="panel rounded-[2rem] p-6">
                <p className="text-sm uppercase tracking-[0.22em] text-slate-400">
                  Hipertensi
                </p>
                <p className="heading-font mt-4 text-5xl font-bold text-slate-900">
                  {Math.round(item.hypertension.percentage)}%
                </p>
                <p className="mt-2 font-semibold text-[var(--danger)]">
                  {categoryLabel(item.hypertension.category)}
                </p>
                <p className="mt-4 text-sm leading-7 text-muted">
                  {item.hypertension.note}
                </p>
              </div>

              <div className="panel rounded-[2rem] p-6">
                <p className="text-sm uppercase tracking-[0.22em] text-slate-400">
                  Kardiovaskular
                </p>
                <p className="heading-font mt-4 text-5xl font-bold text-slate-900">
                  {Math.round(item.cardiovascular.percentage)}%
                </p>
                <p className="mt-2 font-semibold text-[var(--primary)]">
                  {categoryLabel(item.cardiovascular.category)}
                </p>
                <p className="mt-4 text-sm leading-7 text-muted">
                  {item.cardiovascular.note}
                </p>
              </div>
            </div>

            <div className="panel rounded-[2rem] p-6">
              <p className="text-sm uppercase tracking-[0.22em] text-slate-400">
                Saran Lengkap
              </p>

              <div className="mt-5 grid gap-4 md:grid-cols-2">
                {item.hypertension.suggestions.map((suggestion) => (
                  <div key={suggestion} className="rounded-[1.4rem] bg-slate-100/90 p-5">
                    <p className="font-semibold text-slate-900">{suggestion}</p>
                  </div>
                ))}

                {item.cardiovascular.suggestions.map((suggestion) => (
                  <div key={suggestion} className="rounded-[1.4rem] bg-slate-100/90 p-5">
                    <p className="font-semibold text-slate-900">{suggestion}</p>
                  </div>
                ))}
              </div>
            </div>

            <Link
              href="/history"
              className="inline-flex rounded-2xl bg-[var(--primary)] px-5 py-3 font-semibold !text-white"
            >
              Kembali ke Riwayat
            </Link>
          </div>
        ) : null}
      </section>
    </AppShell>
  );
}