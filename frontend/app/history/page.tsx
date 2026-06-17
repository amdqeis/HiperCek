"use client";

import { AppShell } from "@/components/app-shell";
import { TrashIcon } from "@/components/icons";
import { deletePredictionHistory, getPredictionHistory } from "@/lib/api";
import type { PredictionResponse } from "@/lib/types";
import { useEffect, useState } from "react";
import Link from "next/link";

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

export default function HistoryPage() {
  const [items, setItems] = useState<PredictionResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let active = true;

    async function load() {
      try {
        const response = await getPredictionHistory();
        if (active) {
          setItems(response);
          setError(null);
        }
      } catch (loadError) {
        if (active) {
          setError(loadError instanceof Error ? loadError.message : "Gagal memuat riwayat.");
        }
      } finally {
        if (active) {
          setLoading(false);
        }
      }
    }

    void load();
    return () => {
      active = false;
    };
  }, []);

  async function handleDelete(id: string) {
    try {
      await deletePredictionHistory(id);
      setItems((current) => current.filter((item) => item.id !== id));
    } catch (deleteError) {
      setError(deleteError instanceof Error ? deleteError.message : "Gagal menghapus riwayat.");
    }
  }

  return (
    <AppShell active="history">
      <section className="mx-auto max-w-[1080px]">
        <header className="mb-8">
          <h1 className="heading-font text-4xl font-bold tracking-[-0.03em]">Riwayat Prediksi</h1>
          <p className="mt-4 text-lg leading-8 text-muted">
            Riwayat Hasil prediksi akan tersimpan dan dapat dilihat disini
          </p>
        </header>

        {error ? (
          <div className="mb-6 rounded-[1.4rem] border border-red-100 bg-red-50 px-5 py-4 text-sm text-red-700">
            {error}
          </div>
        ) : null}

        {loading ? (
          <div className="panel rounded-[2rem] p-8 text-lg text-muted">Memuat riwayat prediksi...</div>
        ) : null}

        {!loading && items.length === 0 ? (
          <div className="panel rounded-[2.4rem] px-8 py-14 text-center">
            <p className="heading-font text-3xl font-bold text-slate-900">Belum Ada Riwayat</p>
            <p className="mx-auto mt-4 max-w-2xl text-lg leading-8 text-muted">
              Setelah Anda menjalankan prediksi dari halaman form, hasilnya akan muncul di sini dan
              bisa dihapus kapan saja.
            </p>
          </div>
        ) : null}

        <div className="space-y-5">
          {items.map((item) => (
            <article key={item.id} className="panel rounded-[2rem] p-6">
              <div className="mb-4 flex items-center justify-between gap-4">
                <p className="text-sm uppercase tracking-[0.22em] text-slate-400">
                  {new Date(item.createdAt).toLocaleString("id-ID", {
                    dateStyle: "medium",
                    timeStyle: "short",
                  })}
                </p>

                <div className="flex items-center gap-3">
                  <Link
                    href={`/history/${item.id}`}
                    className="inline-flex items-center rounded-2xl border border-blue-100 bg-blue-50 px-4 py-3 font-semibold text-blue-600 transition hover:bg-blue-100"
                  >
                    Rincian
                  </Link>

                  <button
                    className="inline-flex items-center gap-2 rounded-2xl border border-red-100 bg-white px-4 py-3 font-semibold text-red-600"
                    type="button"
                    onClick={() => handleDelete(item.id)}
                  >
                    <TrashIcon className="h-5 w-5" />
                    Hapus
                  </button>
                </div>
              </div>

              <div className="grid w-full gap-4 md:grid-cols-2">
                <div className="rounded-[1.4rem] bg-slate-100/90 p-5">
                  <p className="text-sm uppercase tracking-[0.22em] text-slate-400">
                    Hipertensi
                  </p>
                  <p className="heading-font mt-3 text-4xl font-bold text-slate-900">
                    {Math.round(item.hypertension.percentage)}%
                  </p>
                  <p className="mt-2 font-semibold text-[var(--danger)]">
                    {categoryLabel(item.hypertension.category)}
                  </p>
                  <p className="mt-3 text-sm leading-7 text-muted">
                    {item.hypertension.note}
                  </p>
                </div>

                <div className="rounded-[1.4rem] bg-slate-100/90 p-5">
                  <p className="text-sm uppercase tracking-[0.22em] text-slate-400">
                    Kardiovaskular
                  </p>
                  <p className="heading-font mt-3 text-4xl font-bold text-slate-900">
                    {Math.round(item.cardiovascular.percentage)}%
                  </p>
                  <p className="mt-2 font-semibold text-[var(--primary)]">
                    {categoryLabel(item.cardiovascular.category)}
                  </p>
                  <p className="mt-3 text-sm leading-7 text-muted">
                    {item.cardiovascular.note}
                  </p>
                </div>
              </div>
            </article>
          ))}
        </div>
      </section>
    </AppShell>
  );
}
