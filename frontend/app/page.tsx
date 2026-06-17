import Link from "next/link";

function FeatureCard({
  title,
  description,
  tone,
}: {
  title: string;
  description: string;
  tone: "blue" | "red" | "rose";
}) {
  const iconTone =
    tone === "blue"
      ? "bg-blue-50 text-blue-700"
      : tone === "red"
        ? "bg-red-50 text-red-700"
        : "bg-rose-50 text-rose-700";

  return (
    <article className="panel rounded-[2rem] p-7">
      <div className={`mb-5 inline-flex h-11 w-11 items-center justify-center rounded-2xl ${iconTone}`}>
        <span className="text-xl">+</span>
      </div>
      <h3 className="mb-2 text-lg font-semibold">{title}</h3>
      <p className="text-sm leading-7 text-muted">{description}</p>
    </article>
  );
}

export default function Home() {
  return (
    <main className="min-h-screen overflow-hidden">
      <section className="mx-auto flex min-h-screen w-full max-w-[1240px] flex-col px-6 pb-10 pt-6 lg:px-10">
        <header className="flex items-center justify-between rounded-[2rem] border border-white/70 bg-white/80 px-6 py-5 shadow-[0_12px_30px_rgba(32,78,172,0.08)] backdrop-blur">
          <div>
            <p className="heading-font text-[2rem] font-bold text-[var(--primary)]">MedCheck</p>
          </div>
          <nav className="hidden items-center gap-8 text-sm text-muted md:flex">
            <Link href="/predict">Mulai Prediksi</Link>
            <Link href="/history">Riwayat</Link>
          </nav>
        </header>

        <div className="grid flex-1 items-center gap-10 py-12 lg:grid-cols-[1.02fr_0.98fr] lg:py-20">
          <div className="max-w-xl">
            <div className="mb-8 inline-flex items-center gap-2 rounded-full bg-[var(--primary-soft)] px-4 py-2 text-sm font-semibold text-[var(--primary)]">
              <span className="inline-block h-2.5 w-2.5 rounded-full bg-[var(--primary)]" />
              Terpercaya & Akurat
            </div>
            <h1 className="heading-font max-w-lg text-5xl font-bold leading-tight tracking-[-0.04em] text-slate-900 md:text-6xl">
              Cek Hipertensi Anda Sekarang
            </h1>
            <p className="mt-7 max-w-xl text-xl leading-9 text-muted">
              Analisis risiko kesehatan Anda dengan teknologi prediksi medis terkini yang
              dirancang untuk kenyamanan di rumah.
            </p>
            <div className="mt-10 flex flex-col gap-4 sm:flex-row">
              <Link className="button-primary min-w-[220px]" href="/predict">
                Mulai Prediksi
                <span aria-hidden="true">→</span>
              </Link>
              <Link className="button-secondary min-w-[220px]" href="/history">
                Lihat Riwayat
              </Link>
            </div>
          </div>

          <div className="relative">
            <div className="panel relative mx-auto max-w-[510px] overflow-hidden rounded-[2.4rem] bg-[radial-gradient(circle_at_center,_rgba(53,207,227,0.28),_rgba(7,18,24,0.98)_48%)] p-6">
              <div className="aspect-[0.88] w-full rounded-[2rem] border border-cyan-400/10 bg-[radial-gradient(circle_at_center,_rgba(50,232,252,0.35),_transparent_18%),radial-gradient(circle_at_center,_rgba(58,164,255,0.17),_transparent_36%),radial-gradient(circle_at_center,_rgba(255,255,255,0.08),_transparent_48%)]">
                <div className="relative h-full w-full">
                  <div className="absolute inset-10 rounded-full border border-cyan-300/18" />
                  <div className="absolute inset-20 rounded-full border border-cyan-300/25" />
                  <div className="absolute inset-30 rounded-full border border-cyan-300/15" />
                  <div className="absolute left-1/2 top-0 h-full w-px -translate-x-1/2 bg-cyan-200/20" />
                  <div className="absolute top-1/2 h-px w-full -translate-y-1/2 bg-cyan-200/20" />
                </div>
              </div>

              <div className="absolute inset-x-6 bottom-6 rounded-[1.6rem] bg-white/92 px-7 py-6 shadow-[0_16px_40px_rgba(0,0,0,0.18)] backdrop-blur">
                <div className="grid grid-cols-[auto_1fr_auto] items-center gap-4">
                  <div className="flex h-12 w-12 items-center justify-center rounded-full bg-[var(--primary-soft)] text-xl text-[var(--primary)]">
                    ♥
                  </div>
                  <div>
                    <p className="text-xs uppercase tracking-[0.24em] text-slate-400">Status Jantung</p>
                    <p className="heading-font text-2xl font-bold">Normal</p>
                  </div>
                  <div className="text-right">
                    <p className="text-xs uppercase tracking-[0.24em] text-slate-400">Tekanan</p>
                    <p className="heading-font text-3xl font-bold text-[var(--primary)]">120/80</p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <section className="grid gap-6 lg:grid-cols-3">
          <FeatureCard
            title="Proses Cepat"
            description="Hasil instan dalam hitungan detik setelah data dimasukkan."
            tone="blue"
          />
          <FeatureCard
            title="Data Aman"
            description="Privasi Anda prioritas utama. Enkripsi tingkat medis."
            tone="red"
          />
          <FeatureCard
            title="AI Medis"
            description="Algoritma cerdas berbasis riset kesehatan terbaru."
            tone="rose"
          />
        </section>

        <footer className="mt-12 flex flex-col items-center justify-between gap-4 border-t border-white/80 px-2 py-8 text-sm text-muted md:flex-row">
          <p className="heading-font text-2xl font-bold text-[var(--primary)]">MedCheck</p>
          <p>© 2026 MedCheck. Solusi analisis hipertensi modern.</p>
          <div className="flex items-center gap-6">
            <span>Privasi</span>
            <span>Syarat</span>
            <span>Bantuan</span>
          </div>
        </footer>
      </section>
    </main>
  );
}
