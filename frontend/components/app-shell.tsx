import Link from "next/link";
import type { ReactNode } from "react";
import { BellIcon, ClipboardIcon, GearIcon, HistoryIcon } from "./icons";

export function AppShell({
  active,
  children,
}: {
  active: "predict" | "history";
  children: ReactNode;
}) {
  const navItems = [
    { href: "/predict", label: "Buat Prediksi", key: "predict" as const, icon: ClipboardIcon },
    { href: "/history", label: "Riwayat Prediksi", key: "history" as const, icon: HistoryIcon },
  ];

  return (
    <div className="app-grid">
      <aside className="border-r border-white/70 bg-white/72 px-4 py-6 backdrop-blur lg:px-5">
        <Link href="/" className="mb-10 block rounded-[1.8rem] px-2 py-2">
          <p className="heading-font text-3xl font-bold text-[var(--primary)]">MedCheck</p>
          <p className="mt-1 text-sm uppercase tracking-[0.2em] text-slate-400">Health Analysis</p>
        </Link>

        <nav className="space-y-2">
          {navItems.map(({ href, label, key, icon: Icon }) => {
            const activeItem = key === active;
            return (
              <Link
                key={href}
                href={href}
                className={`flex items-center gap-3 rounded-2xl px-4 py-4 text-base font-semibold transition ${
                  activeItem
                    ? "bg-[var(--primary-soft)] text-[var(--primary)] shadow-[inset_-3px_0_0_var(--primary)]"
                    : "text-slate-500 hover:bg-white/80 hover:text-slate-900"
                }`}
              >
                <Icon className="h-5 w-5" />
                <span>{label}</span>
              </Link>
            );
          })}
        </nav>
      </aside>

      <div className="min-w-0">
        <header className="flex items-center justify-between border-b border-white/70 bg-white/72 px-6 py-5 backdrop-blur lg:px-8">
          <Link href="/" className="heading-font text-4xl font-bold text-[var(--primary)]">
            MedCheck
          </Link>
          <div className="flex items-center gap-4 text-slate-500">
            <BellIcon className="h-6 w-6" />
            <GearIcon className="h-6 w-6" />
          </div>
        </header>

        <main className="px-5 py-8 lg:px-8">{children}</main>
      </div>
    </div>
  );
}
