"use client";

import { FormEvent, useEffect, useState } from "react";
import { OpsShell } from "../../../components/OpsShell";
import { apiGet, apiPost } from "../../../lib/api";
import styles from "../../page.module.css";

type Campaign = {
  id?: string;
  name?: string;
  status?: string;
  reviewers?: string[];
  [key: string]: unknown;
};

export default function CertificationsPage() {
  const [campaigns, setCampaigns] = useState<Campaign[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [result, setResult] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);

  async function refresh() {
    setCampaigns(await apiGet<Campaign[]>("/v1/certifications"));
  }

  useEffect(() => {
    refresh().catch((err: Error) => setError(err.message));
  }, []);

  async function createCampaign(e: FormEvent<HTMLFormElement>) {
    e.preventDefault();
    const form = new FormData(e.currentTarget);
    setBusy(true);
    setError(null);
    try {
      const campaign = await apiPost<Campaign>("/v1/certifications/campaigns", {
        name: String(form.get("name") || "Q3 Access Review"),
        reviewers: String(form.get("reviewers") || "manager@example.com")
          .split(",")
          .map((s) => s.trim())
          .filter(Boolean),
      });
      setResult(JSON.stringify(campaign, null, 2));
      await refresh();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Create failed");
    } finally {
      setBusy(false);
    }
  }

  async function evaluateSod(e: FormEvent<HTMLFormElement>) {
    e.preventDefault();
    const form = new FormData(e.currentTarget);
    setBusy(true);
    setError(null);
    try {
      const sod = await apiPost<Record<string, unknown>>("/v1/certifications/sod/evaluate", {
        entitlements: String(form.get("entitlements") || "payroll-admin,payroll-auditor")
          .split(",")
          .map((s) => s.trim())
          .filter(Boolean),
      });
      setResult(JSON.stringify(sod, null, 2));
    } catch (err) {
      setError(err instanceof Error ? err.message : "SoD evaluate failed");
    } finally {
      setBusy(false);
    }
  }

  return (
    <OpsShell
      title="Certifications"
      lead="V3 access certification campaigns and segregation-of-duties evaluation."
      activeHref="/ops/certifications"
    >
      {error ? (
        <div className={styles.errorBanner} role="alert">
          {error}
        </div>
      ) : null}

      <div className={styles.panel}>
        <h2 className={styles.panelTitle}>Create campaign</h2>
        <form onSubmit={createCampaign}>
          <div className={styles.formGrid}>
            <div className={styles.field}>
              <label htmlFor="name">Name</label>
              <input id="name" name="name" defaultValue="Q3 Access Review" />
            </div>
            <div className={styles.field}>
              <label htmlFor="reviewers">Reviewers</label>
              <input id="reviewers" name="reviewers" defaultValue="manager@example.com" />
            </div>
          </div>
          <div className={styles.formActions}>
            <button className={styles.primary} type="submit" disabled={busy}>
              Create campaign
            </button>
          </div>
        </form>
      </div>

      <div className={styles.panel}>
        <h2 className={styles.panelTitle}>Evaluate SoD</h2>
        <form onSubmit={evaluateSod}>
          <div className={styles.formGrid}>
            <div className={`${styles.field} ${styles.fieldWide}`}>
              <label htmlFor="entitlements">Entitlements</label>
              <input
                id="entitlements"
                name="entitlements"
                defaultValue="payroll-admin,payroll-auditor"
              />
            </div>
          </div>
          <div className={styles.formActions}>
            <button className={styles.secondary} type="submit" disabled={busy}>
              Evaluate
            </button>
          </div>
        </form>
      </div>

      {result ? <pre className={styles.resultBox}>{result}</pre> : null}

      <div className={styles.tableWrap}>
        {campaigns.length === 0 ? (
          <div className={styles.empty}>No certification campaigns yet.</div>
        ) : (
          <table className={styles.table}>
            <thead>
              <tr>
                <th scope="col">Campaign</th>
                <th scope="col">Status</th>
                <th scope="col">Reviewers</th>
              </tr>
            </thead>
            <tbody>
              {campaigns.map((c, i) => (
                <tr key={String(c.id ?? i)}>
                  <td>
                    <div className={styles.vendor}>{String(c.name ?? "—")}</div>
                    <div className={styles.meta}>{String(c.id ?? "")}</div>
                  </td>
                  <td>{String(c.status ?? "—")}</td>
                  <td className={styles.meta}>
                    {Array.isArray(c.reviewers) ? c.reviewers.join(", ") : "—"}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </OpsShell>
  );
}
