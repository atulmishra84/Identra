"use client";

import { useEffect, useState } from "react";
import { OpsShell } from "../../../components/OpsShell";
import { apiGet } from "../../../lib/api";
import styles from "../../page.module.css";

type MarketItem = {
  id: string;
  type: string;
  name: string;
  description: string;
  visibility: string;
  signed: boolean;
  scorecard?: { overallScore?: number };
};

type MarketStatus = {
  itemCount: number;
  publicEnabled: boolean;
  defaultVisibility: string;
};

export default function MarketplacePage() {
  const [items, setItems] = useState<MarketItem[]>([]);
  const [status, setStatus] = useState<MarketStatus | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let cancelled = false;
    Promise.all([
      apiGet<MarketItem[]>("/v1/marketplace/items"),
      apiGet<MarketStatus>("/v1/marketplace/status"),
    ])
      .then(([list, st]) => {
        if (!cancelled) {
          setItems(list);
          setStatus(st);
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

  return (
    <OpsShell
      title="Marketplace"
      lead="V2 private-tenant catalog for connectors, workflows, and automation packs."
      activeHref="/ops/marketplace"
    >
      <div className={styles.statusRow}>
        <span className={styles.pill}>
          <span className={`${styles.pillDot} ${styles.ok}`} />
          {status
            ? `${status.itemCount} items · ${status.publicEnabled ? "public on" : "private"}`
            : "Loading status…"}
        </span>
        <span className={styles.pill}>
          <span className={styles.pillDot} />
          Default: {status?.defaultVisibility ?? "…"}
        </span>
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
          </>
        ) : items.length === 0 ? (
          <div className={styles.empty}>No marketplace items yet.</div>
        ) : (
          <table className={styles.table}>
            <thead>
              <tr>
                <th scope="col">Name</th>
                <th scope="col">Type</th>
                <th scope="col">Visibility</th>
                <th scope="col">Score</th>
              </tr>
            </thead>
            <tbody>
              {items.map((item) => (
                <tr key={item.id}>
                  <td>
                    <div className={styles.vendor}>{item.name}</div>
                    <div className={styles.meta}>{item.description}</div>
                  </td>
                  <td>{item.type}</td>
                  <td className={styles.meta}>
                    {item.visibility}
                    {item.signed ? " · signed" : ""}
                  </td>
                  <td>{item.scorecard?.overallScore ?? "—"}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </OpsShell>
  );
}
