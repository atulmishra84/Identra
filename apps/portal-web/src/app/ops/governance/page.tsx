"use client";

import { FormEvent, useState } from "react";
import { OpsShell } from "../../../components/OpsShell";
import { apiPost } from "../../../lib/api";
import styles from "../../page.module.css";

export default function GovernancePage() {
  const [result, setResult] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);

  async function onSubmit(e: FormEvent<HTMLFormElement>) {
    e.preventDefault();
    const form = new FormData(e.currentTarget);
    setBusy(true);
    setError(null);
    try {
      const score = await apiPost<Record<string, unknown>>("/v1/governance/score", {
        name: String(form.get("name") || "connector-pack"),
        type: String(form.get("type") || "connector"),
        coverage: Number(form.get("coverage") || 80),
        security: Number(form.get("security") || 80),
        compliance: Number(form.get("compliance") || 80),
        actions: String(form.get("actions") || "Create User,Disable User")
          .split(",")
          .map((s) => s.trim())
          .filter(Boolean),
      });
      setResult(JSON.stringify(score, null, 2));
    } catch (err) {
      setError(err instanceof Error ? err.message : "Scoring failed");
    } finally {
      setBusy(false);
    }
  }

  return (
    <OpsShell
      title="Governance"
      lead="V2 connector scorecards — coverage, security, compliance, and overall publish gates."
      activeHref="/ops/governance"
    >
      {error ? (
        <div className={styles.errorBanner} role="alert">
          {error}
        </div>
      ) : null}

      <div className={styles.panel}>
        <h2 className={styles.panelTitle}>Score a pack</h2>
        <form onSubmit={onSubmit}>
          <div className={styles.formGrid}>
            <div className={styles.field}>
              <label htmlFor="name">Name</label>
              <input id="name" name="name" defaultValue="okta-saas-baseline" />
            </div>
            <div className={styles.field}>
              <label htmlFor="type">Type</label>
              <select id="type" name="type" defaultValue="connector">
                <option>connector</option>
                <option>automation</option>
                <option>workflow</option>
              </select>
            </div>
            <div className={styles.field}>
              <label htmlFor="coverage">Coverage</label>
              <input id="coverage" name="coverage" type="number" defaultValue={85} min={0} max={100} />
            </div>
            <div className={styles.field}>
              <label htmlFor="security">Security</label>
              <input id="security" name="security" type="number" defaultValue={90} min={0} max={100} />
            </div>
            <div className={styles.field}>
              <label htmlFor="compliance">Compliance</label>
              <input id="compliance" name="compliance" type="number" defaultValue={88} min={0} max={100} />
            </div>
            <div className={styles.field}>
              <label htmlFor="actions">Actions</label>
              <input id="actions" name="actions" defaultValue="Create User,Disable User,Reset Password" />
            </div>
          </div>
          <div className={styles.formActions}>
            <button className={styles.primary} type="submit" disabled={busy}>
              {busy ? "Scoring…" : "Score"}
            </button>
          </div>
        </form>
        {result ? <pre className={styles.resultBox}>{result}</pre> : null}
      </div>
    </OpsShell>
  );
}
