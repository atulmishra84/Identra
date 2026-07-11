"use client";

import { useEffect, useMemo, useState } from "react";
import styles from "../page.module.css";

type AdapterRow = {
  adapterId: string;
  vendor: string;
  version: string;
  health: string;
  swapCompatibleWith?: string;
};

const TENANT = "11111111-1111-1111-1111-111111111111";
const GATEWAY = process.env.NEXT_PUBLIC_IDENTRA_GATEWAY_URL ?? "/identra";

function healthTone(health: string): "ok" | "warn" | "danger" {
  const h = health.toLowerCase();
  if (h.includes("up") || h.includes("healthy") || h.includes("ok")) return "ok";
  if (h.includes("degrad") || h.includes("warn")) return "warn";
  return "danger";
}

export default function OpsPage() {
  const [adapters, setAdapters] = useState<AdapterRow[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let cancelled = false;
    setLoading(true);
    fetch(`${GATEWAY}/v1/adapters`, {
      headers: { "X-Tenant-Id": TENANT },
    })
      .then(async (res) => {
        if (!res.ok) {
          throw new Error(`Gateway returned ${res.status}`);
        }
        return res.json();
      })
      .then((data) => {
        if (!cancelled) {
          setAdapters(Array.isArray(data) ? data : []);
          setError(null);
        }
      })
      .catch((err: Error) => {
        if (!cancelled) setError(err.message);
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });
    return () => {
      cancelled = true;
    };
  }, []);

  const healthyCount = useMemo(
    () => adapters.filter((a) => healthTone(a.health) === "ok").length,
    [adapters],
  );

  return (
    <div className={styles.opsShell}>
      <header className={styles.opsHeader}>
        <a className={styles.opsBrand} href="/">
          <span className={styles.logoGlyph} aria-hidden="true" />
          Identra
        </a>
        <nav className={styles.opsNav} aria-label="Ops">
          <a href="/">Home</a>
          <span aria-current="page">Ops console</span>
        </nav>
      </header>

      <main className={styles.opsMain}>
        <h1 className={styles.opsTitle}>Ops console</h1>
        <p className={styles.opsLead}>
          Live adapter health and swap targets for the Identra control plane.
        </p>

        <div className={styles.statusRow}>
          <span className={styles.pill}>
            <span
              className={`${styles.pillDot} ${error ? styles.danger : styles.ok}`}
            />
            {error ? "Gateway unreachable" : "Gateway connected"}
          </span>
          <span className={styles.pill}>
            <span className={styles.pillDot} />
            {loading ? "Loading adapters…" : `${adapters.length} adapters`}
          </span>
          {!loading && !error ? (
            <span className={styles.pill}>
              <span className={`${styles.pillDot} ${styles.ok}`} />
              {healthyCount} healthy
            </span>
          ) : null}
        </div>

        {error ? (
          <div className={styles.errorBanner} role="alert">
            Could not load adapters: {error}
          </div>
        ) : null}

        <div className={styles.tableWrap}>
          {loading ? (
            <>
              <div className={styles.skeleton} />
              <div className={styles.skeleton} />
              <div className={styles.skeleton} />
            </>
          ) : adapters.length === 0 && !error ? (
            <div className={styles.empty}>No adapters registered yet.</div>
          ) : (
            <table className={styles.table}>
              <thead>
                <tr>
                  <th scope="col">Vendor</th>
                  <th scope="col">Health</th>
                  <th scope="col">Swap target</th>
                </tr>
              </thead>
              <tbody>
                {adapters.map((adapter) => {
                  const tone = healthTone(adapter.health);
                  return (
                    <tr key={adapter.adapterId}>
                      <td>
                        <div className={styles.vendor}>{adapter.vendor}</div>
                        <div className={styles.meta}>
                          {adapter.adapterId} · v{adapter.version}
                        </div>
                      </td>
                      <td>
                        <span className={styles.health}>
                          <span className={`${styles.pillDot} ${styles[tone]}`} />
                          {adapter.health}
                        </span>
                      </td>
                      <td className={styles.meta}>
                        {adapter.swapCompatibleWith || "—"}
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          )}
        </div>
      </main>
    </div>
  );
}
