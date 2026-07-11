"use client";

import { FormEvent, useEffect, useState } from "react";
import { OpsShell } from "../../../components/OpsShell";
import { apiGet, apiPost } from "../../../lib/api";
import styles from "../../page.module.css";

type ConnectorRun = {
  id?: string;
  packageId?: string;
  application?: string;
  iamPlatform?: string;
  status?: string;
  [key: string]: unknown;
};

export default function ConnectorsPage() {
  const [runs, setRuns] = useState<ConnectorRun[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [result, setResult] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);

  async function refresh() {
    const data = await apiGet<ConnectorRun[]>("/v1/ai/connector-factory/runs");
    setRuns(data);
  }

  useEffect(() => {
    refresh().catch((err: Error) => setError(err.message));
  }, []);

  async function onSubmit(e: FormEvent<HTMLFormElement>) {
    e.preventDefault();
    const form = new FormData(e.currentTarget);
    setBusy(true);
    setError(null);
    try {
      const created = await apiPost<ConnectorRun>("/v1/ai/connector-factory/runs", {
        iamPlatform: String(form.get("iamPlatform") || "SailPoint ISC"),
        application: String(form.get("application") || "SAP"),
        authenticationType: String(form.get("authenticationType") || "REST"),
        actions: String(form.get("actions") || "Create User,Disable User")
          .split(",")
          .map((s) => s.trim())
          .filter(Boolean),
      });
      setResult(JSON.stringify(created, null, 2));
      await refresh();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Generate failed");
    } finally {
      setBusy(false);
    }
  }

  return (
    <OpsShell
      title="Connector factory"
      lead="V2 AI-assisted connector generation — produce packs scored by governance before publish."
      activeHref="/ops/connectors"
    >
      {error ? (
        <div className={styles.errorBanner} role="alert">
          {error}
        </div>
      ) : null}

      <div className={styles.panel}>
        <h2 className={styles.panelTitle}>Generate connector pack</h2>
        <form onSubmit={onSubmit}>
          <div className={styles.formGrid}>
            <div className={styles.field}>
              <label htmlFor="iamPlatform">IAM platform</label>
              <input id="iamPlatform" name="iamPlatform" defaultValue="SailPoint ISC" />
            </div>
            <div className={styles.field}>
              <label htmlFor="application">Application</label>
              <input id="application" name="application" defaultValue="SAP" />
            </div>
            <div className={styles.field}>
              <label htmlFor="authenticationType">Auth type</label>
              <select id="authenticationType" name="authenticationType" defaultValue="REST">
                <option>REST</option>
                <option>OAuth2</option>
                <option>SAML</option>
                <option>Basic</option>
              </select>
            </div>
            <div className={styles.field}>
              <label htmlFor="actions">Actions (comma-separated)</label>
              <input
                id="actions"
                name="actions"
                defaultValue="Create User,Disable User,Delete User,Reset Password"
              />
            </div>
          </div>
          <div className={styles.formActions}>
            <button className={styles.primary} type="submit" disabled={busy}>
              {busy ? "Generating…" : "Generate"}
            </button>
          </div>
        </form>
        {result ? <pre className={styles.resultBox}>{result}</pre> : null}
      </div>

      <div className={styles.tableWrap}>
        {runs.length === 0 ? (
          <div className={styles.empty}>No connector runs yet — generate one above.</div>
        ) : (
          <table className={styles.table}>
            <thead>
              <tr>
                <th scope="col">Run</th>
                <th scope="col">Application</th>
                <th scope="col">IAM</th>
              </tr>
            </thead>
            <tbody>
              {runs.map((run, i) => (
                <tr key={String(run.id ?? run.packageId ?? i)}>
                  <td className={styles.meta}>{String(run.id ?? run.packageId ?? "—")}</td>
                  <td className={styles.vendor}>{String(run.application ?? "—")}</td>
                  <td>{String(run.iamPlatform ?? "—")}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </OpsShell>
  );
}
