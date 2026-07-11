"use client";

import { useEffect, useState } from "react";
import { OpsShell, OPS_NAV } from "../../components/OpsShell";
import { apiGet } from "../../lib/api";
import styles from "../page.module.css";

type StatusMap = Record<string, string>;

export default function OpsOverviewPage() {
  const [status, setStatus] = useState<StatusMap>({});
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let cancelled = false;
    async function load() {
      try {
        const [adapters, market, ai, certs, autos, connectors] = await Promise.all([
          apiGet<unknown[]>("/v1/adapters"),
          apiGet<{ itemCount: number; publicEnabled: boolean }>("/v1/marketplace/status"),
          apiGet<{ defaultProvider: string; providers: string[] }>("/v1/ai/status"),
          apiGet<unknown[]>("/v1/certifications"),
          apiGet<unknown[]>("/v1/ai/automation-factory/runs"),
          apiGet<unknown[]>("/v1/ai/connector-factory/runs"),
        ]);
        if (cancelled) return;
        setStatus({
          adapters: `${adapters.length} registered`,
          marketplace: `${market.itemCount} items · ${market.publicEnabled ? "public" : "private"}`,
          ai: `${ai.defaultProvider} (${ai.providers.join(", ")})`,
          certifications: `${certs.length} campaigns`,
          automation: `${autos.length} packs`,
          connectors: `${connectors.length} runs`,
        });
      } catch (err) {
        if (!cancelled) setError(err instanceof Error ? err.message : "Failed to load");
      }
    }
    void load();
    return () => {
      cancelled = true;
    };
  }, []);

  const descriptions: Record<string, string> = {
    "/ops": "Live status across V1–V3 modules.",
    "/ops/adapters": "Vendor adapter catalog and swap targets.",
    "/ops/marketplace": "Private connector and automation catalog.",
    "/ops/connectors": "AI-assisted connector generation.",
    "/ops/migrations": "Cross-IAM export / transform / import dry-runs.",
    "/ops/ai": "LLM router, providers, and token budget.",
    "/ops/governance": "Score connectors for coverage and security.",
    "/ops/core": "Bootstrap IAM — identities, roles, access requests.",
    "/ops/automation": "Generate Playwright/Selenium/UiPath packs.",
    "/ops/certifications": "Access certification campaigns and SoD checks.",
  };

  return (
    <OpsShell
      title="Platform console"
      lead="Operate Identra Fabric (V1), AI/migration platform (V2), and automation/certification (V3) from one control plane."
      activeHref="/ops"
    >
      {error ? (
        <div className={styles.errorBanner} role="alert">
          {error}
        </div>
      ) : null}

      <div className={styles.statusRow}>
        <span className={styles.pill}>
          <span className={`${styles.pillDot} ${styles.ok}`} />
          V1 Adapters · {status.adapters ?? "…"}
        </span>
        <span className={styles.pill}>
          <span className={`${styles.pillDot} ${styles.ok}`} />
          V2 Marketplace · {status.marketplace ?? "…"}
        </span>
        <span className={styles.pill}>
          <span className={`${styles.pillDot} ${styles.ok}`} />
          V2 AI · {status.ai ?? "…"}
        </span>
        <span className={styles.pill}>
          <span className={`${styles.pillDot} ${styles.ok}`} />
          V3 Certs · {status.certifications ?? "…"}
        </span>
      </div>

      <div className={styles.moduleGrid}>
        {OPS_NAV.filter((item) => item.href !== "/ops").map((item) => (
          <a key={item.href} href={item.href} className={styles.moduleLink}>
            <span className={styles.moduleVersion}>{item.version}</span>
            <strong>{item.label}</strong>
            <p>{descriptions[item.href]}</p>
          </a>
        ))}
      </div>
    </OpsShell>
  );
}
