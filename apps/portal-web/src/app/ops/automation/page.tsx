"use client";

import { FormEvent, useEffect, useState } from "react";
import { OpsShell } from "../../../components/OpsShell";
import { apiGet, apiPost } from "../../../lib/api";
import styles from "../../page.module.css";

type AutomationPack = {
  id?: string;
  packId?: string;
  applicationName?: string;
  toolchain?: string;
  targetType?: string;
  [key: string]: unknown;
};

export default function AutomationPage() {
  const [packs, setPacks] = useState<AutomationPack[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [result, setResult] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);

  async function refresh() {
    setPacks(await apiGet<AutomationPack[]>("/v1/ai/automation-factory/runs"));
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
      const pack = await apiPost<AutomationPack>("/v1/ai/automation-factory/runs", {
        applicationName: String(form.get("applicationName") || "LegacyHR"),
        targetType: String(form.get("targetType") || "BROWSER"),
        toolchain: String(form.get("toolchain") || "PLAYWRIGHT"),
        actions: String(form.get("actions") || "Create User,Disable User")
          .split(",")
          .map((s) => s.trim())
          .filter(Boolean),
        hints: {},
      });
      setResult(JSON.stringify(pack, null, 2));
      await refresh();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Generate failed");
    } finally {
      setBusy(false);
    }
  }

  return (
    <OpsShell
      title="Automation"
      lead="V3 automation factory — generate Playwright, Selenium, Robot, UiPath, and Python packs for non-API systems."
      activeHref="/ops/automation"
    >
      {error ? (
        <div className={styles.errorBanner} role="alert">
          {error}
        </div>
      ) : null}

      <div className={styles.panel}>
        <h2 className={styles.panelTitle}>Generate automation pack</h2>
        <form onSubmit={onSubmit}>
          <div className={styles.formGrid}>
            <div className={styles.field}>
              <label htmlFor="applicationName">Application</label>
              <input id="applicationName" name="applicationName" defaultValue="LegacyHR" />
            </div>
            <div className={styles.field}>
              <label htmlFor="targetType">Target type</label>
              <select id="targetType" name="targetType" defaultValue="BROWSER">
                <option>BROWSER</option>
                <option>DESKTOP</option>
                <option>TERMINAL</option>
                <option>CITRIX</option>
                <option>SAP_GUI</option>
                <option>ORACLE_FORMS</option>
                <option>LEGACY</option>
              </select>
            </div>
            <div className={styles.field}>
              <label htmlFor="toolchain">Toolchain</label>
              <select id="toolchain" name="toolchain" defaultValue="PLAYWRIGHT">
                <option>PLAYWRIGHT</option>
                <option>SELENIUM</option>
                <option>ROBOT_FRAMEWORK</option>
                <option>POWER_AUTOMATE</option>
                <option>UIPATH</option>
                <option>PYTHON</option>
              </select>
            </div>
            <div className={styles.field}>
              <label htmlFor="actions">Actions</label>
              <input id="actions" name="actions" defaultValue="Create User,Disable User" />
            </div>
          </div>
          <div className={styles.formActions}>
            <button className={styles.primary} type="submit" disabled={busy}>
              {busy ? "Generating…" : "Generate pack"}
            </button>
          </div>
        </form>
        {result ? <pre className={styles.resultBox}>{result}</pre> : null}
      </div>

      <div className={styles.tableWrap}>
        {packs.length === 0 ? (
          <div className={styles.empty}>No automation packs yet.</div>
        ) : (
          <table className={styles.table}>
            <thead>
              <tr>
                <th scope="col">Pack</th>
                <th scope="col">Application</th>
                <th scope="col">Toolchain</th>
              </tr>
            </thead>
            <tbody>
              {packs.map((pack, i) => (
                <tr key={String(pack.id ?? pack.packId ?? i)}>
                  <td className={styles.meta}>{String(pack.id ?? pack.packId ?? "—")}</td>
                  <td className={styles.vendor}>{String(pack.applicationName ?? "—")}</td>
                  <td>
                    {String(pack.toolchain ?? "—")} · {String(pack.targetType ?? "")}
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
