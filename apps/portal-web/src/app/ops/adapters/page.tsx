"use client";

import { useEffect, useMemo, useState } from "react";
import { OpsShell } from "../../../components/OpsShell";
import { apiGet } from "../../../lib/api";
import styles from "../../page.module.css";

type AdapterRow = {
  adapterId: string;
  vendor: string;
  version: string;
  health: string;
  swapCompatibleWith?: string;
};

function healthTone(health: string): "ok" | "warn" | "danger" {
  const h = health.toLowerCase();
  if (h.includes("up") || h.includes("healthy") || h.includes("ok")) return "ok";
  if (h.includes("degrad") || h.includes("warn")) return "warn";
  return "danger";
}

export default function AdaptersPage() {
  const [adapters, setAdapters] = useState<AdapterRow[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let cancelled = false;
    apiGet<AdapterRow[]>("/v1/adapters")
      .then((data) => {
        if (!cancelled) {
          setAdapters(data);
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
    <OpsShell
      title="Adapters"
      lead="V1 Identity Fabric adapter catalog — health and swap-compatible targets."
      activeHref="/ops/adapters"
    >
      <div className={styles.statusRow}>
        <span className={styles.pill}>
          <span className={`${styles.pillDot} ${error ? styles.danger : styles.ok}`} />
          {error ? "Gateway unreachable" : "Gateway connected"}
        </span>
        <span className={styles.pill}>
          <span className={styles.pillDot} />
          {loading ? "Loading…" : `${adapters.length} adapters`}
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
          {error}
        </div>
      ) : null}

      <div className={styles.tableWrap}>
        {loading ? (
          <>
            <div className={styles.skeleton} />
            <div className={styles.skeleton} />
            <div className={styles.skeleton} />
          </>
        ) : adapters.length === 0 ? (
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
                    <td className={styles.meta}>{adapter.swapCompatibleWith || "—"}</td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        )}
      </div>
    </OpsShell>
  );
}
