"use client";

import { FormEvent, useState } from "react";
import { OpsShell } from "../../../components/OpsShell";
import { apiPost } from "../../../lib/api";
import styles from "../../page.module.css";

export default function MigrationsPage() {
  const [result, setResult] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);

  async function onSubmit(e: FormEvent<HTMLFormElement>) {
    e.preventDefault();
    const form = new FormData(e.currentTarget);
    setBusy(true);
    setError(null);
    try {
      const job = await apiPost<Record<string, unknown>>("/v1/migrations", {
        sourceIam: String(form.get("sourceIam")),
        targetIam: String(form.get("targetIam")),
        include: String(form.get("include") || "roles,identitySchema")
          .split(",")
          .map((s) => s.trim())
          .filter(Boolean),
      });
      setResult(JSON.stringify(job, null, 2));
    } catch (err) {
      setError(err instanceof Error ? err.message : "Migration start failed");
    } finally {
      setBusy(false);
    }
  }

  return (
    <OpsShell
      title="Migrations"
      lead="V2 cross-IAM migration engine — start a dry-run export / transform / import job."
      activeHref="/ops/migrations"
    >
      {error ? (
        <div className={styles.errorBanner} role="alert">
          {error}
        </div>
      ) : null}

      <div className={styles.panel}>
        <h2 className={styles.panelTitle}>Start migration dry-run</h2>
        <form onSubmit={onSubmit}>
          <div className={styles.formGrid}>
            <div className={styles.field}>
              <label htmlFor="sourceIam">Source IAM</label>
              <select id="sourceIam" name="sourceIam" defaultValue="Okta">
                <option>Okta</option>
                <option>SailPoint ISC</option>
                <option>Entra</option>
                <option>Saviynt</option>
                <option>Ping Identity</option>
              </select>
            </div>
            <div className={styles.field}>
              <label htmlFor="targetIam">Target IAM</label>
              <select id="targetIam" name="targetIam" defaultValue="Entra">
                <option>Entra</option>
                <option>Okta</option>
                <option>SailPoint ISC</option>
                <option>Saviynt</option>
                <option>Ping Identity</option>
              </select>
            </div>
            <div className={`${styles.field} ${styles.fieldWide}`}>
              <label htmlFor="include">Include (comma-separated)</label>
              <input id="include" name="include" defaultValue="roles,identitySchema" />
            </div>
          </div>
          <div className={styles.formActions}>
            <button className={styles.primary} type="submit" disabled={busy}>
              {busy ? "Starting…" : "Start dry-run"}
            </button>
          </div>
        </form>
        {result ? <pre className={styles.resultBox}>{result}</pre> : null}
      </div>
    </OpsShell>
  );
}
