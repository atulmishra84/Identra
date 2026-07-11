"use client";

import { useEffect, useState } from "react";
import styles from "../page.module.css";

type AdapterRow = {
  adapterId: string;
  vendor: string;
  version: string;
  health: string;
  swapCompatibleWith?: string;
};

const TENANT = "11111111-1111-1111-1111-111111111111";
const GATEWAY = process.env.NEXT_PUBLIC_IDENTRA_GATEWAY_URL ?? "http://localhost:8080";

export default function OpsPage() {
  const [adapters, setAdapters] = useState<AdapterRow[]>([]);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    fetch(`${GATEWAY}/v1/adapters`, {
      headers: { "X-Tenant-Id": TENANT },
    })
      .then(async (res) => {
        if (!res.ok) {
          throw new Error(`Gateway returned ${res.status}`);
        }
        return res.json();
      })
      .then((data) => setAdapters(Array.isArray(data) ? data : []))
      .catch((err: Error) => setError(err.message));
  }, []);

  return (
    <main className={styles.hero}>
      <p className={styles.brand}>Identra</p>
      <h1 className={styles.title}>Ops console</h1>
      <p className={styles.lead}>
        Adapter health and swap targets for the MVP control plane.
      </p>
      {error ? <p className={styles.lead}>Could not load adapters: {error}</p> : null}
      <ul style={{ listStyle: "none", padding: 0, marginTop: "2rem", width: "100%", maxWidth: 640 }}>
        {adapters.map((adapter) => (
          <li
            key={adapter.adapterId}
            style={{
              borderTop: "1px solid rgba(15,28,46,0.12)",
              padding: "1rem 0",
              display: "flex",
              justifyContent: "space-between",
              gap: "1rem",
            }}
          >
            <div>
              <strong>{adapter.vendor}</strong>
              <div style={{ opacity: 0.7, fontSize: "0.9rem" }}>
                {adapter.adapterId} · {adapter.version}
              </div>
            </div>
            <div style={{ textAlign: "right" }}>
              <div>{adapter.health}</div>
              <div style={{ opacity: 0.7, fontSize: "0.85rem" }}>
                swap → {adapter.swapCompatibleWith}
              </div>
            </div>
          </li>
        ))}
      </ul>
      <div className={styles.actions}>
        <a className={styles.secondary} href="/">
          Home
        </a>
      </div>
    </main>
  );
}
