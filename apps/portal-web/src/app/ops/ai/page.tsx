"use client";

import { useEffect, useState } from "react";
import { OpsShell } from "../../../components/OpsShell";
import { apiGet } from "../../../lib/api";
import styles from "../../page.module.css";

type AiStatus = {
  providers: string[];
  tokenBudget: number;
  tokenBudgetUsed: number;
  defaultProvider: string;
};

export default function AiPage() {
  const [status, setStatus] = useState<AiStatus | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    apiGet<AiStatus>("/v1/ai/status")
      .then(setStatus)
      .catch((err: Error) => setError(err.message));
  }, []);

  return (
    <OpsShell
      title="AI gateway"
      lead="V2 multi-provider LLM router — local template by default, air-gap ready for V3."
      activeHref="/ops/ai"
    >
      {error ? (
        <div className={styles.errorBanner} role="alert">
          {error}
        </div>
      ) : null}

      <div className={styles.statusRow}>
        <span className={styles.pill}>
          <span className={`${styles.pillDot} ${styles.ok}`} />
          Default: {status?.defaultProvider ?? "…"}
        </span>
        <span className={styles.pill}>
          <span className={styles.pillDot} />
          Providers: {status?.providers?.join(", ") ?? "…"}
        </span>
        <span className={styles.pill}>
          <span className={styles.pillDot} />
          Budget: {status ? `${status.tokenBudgetUsed} / ${status.tokenBudget}` : "…"}
        </span>
      </div>

      <div className={styles.panel}>
        <h2 className={styles.panelTitle}>Router status</h2>
        <pre className={styles.resultBox}>
          {status ? JSON.stringify(status, null, 2) : "Loading…"}
        </pre>
      </div>
    </OpsShell>
  );
}
