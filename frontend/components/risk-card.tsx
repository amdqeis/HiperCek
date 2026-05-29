import type { PredictionRisk } from "@/lib/types";

const toneMap = {
  low: {
    badge: "bg-emerald-100 text-emerald-700",
    ring: "#16815b",
    label: "Risiko Rendah",
  },
  medium: {
    badge: "bg-[var(--warning-soft)] text-[var(--warning)]",
    ring: "#1856d0",
    label: "Risiko Sedang",
  },
  high: {
    badge: "bg-[var(--danger-soft)] text-[var(--danger)]",
    ring: "#af3138",
    label: "Risiko Tinggi",
  },
} as const;

export function RiskCard({
  title,
  risk,
}: {
  title: string;
  risk: PredictionRisk;
}) {
  const tone = toneMap[risk.category];
  const percentage = Math.round(risk.percentage);

  return (
    <article className="panel rounded-[2.25rem] p-8">
      <div className={`mx-auto inline-flex rounded-full px-5 py-3 text-sm font-semibold ${tone.badge}`}>
        {tone.label}
      </div>
      <h3 className="mt-5 text-center text-[2rem] font-semibold">{title}</h3>

      <div
        className="ring-chart mx-auto my-8"
        style={{
          background: `conic-gradient(${tone.ring} ${percentage}%, rgba(222, 230, 243, 0.92) ${percentage}% 100%)`,
        }}
      >
        <div className="text-center">
          <p className="heading-font text-6xl font-bold">{percentage}%</p>
          <p className="mt-2 text-sm uppercase tracking-[0.18em] text-slate-400">Skor Risiko</p>
        </div>
      </div>

      <div className="rounded-[1.4rem] bg-slate-100/90 p-5">
        <p className="text-sm uppercase tracking-[0.22em] text-slate-400">Catatan Spesifik</p>
        <p className="mt-3 text-lg leading-8 text-slate-700">{risk.note}</p>
      </div>
    </article>
  );
}
