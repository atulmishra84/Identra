"use client";

import { FormEvent, useState } from "react";
import { OpsShell } from "../../../components/OpsShell";
import { apiPost } from "../../../lib/api";
import styles from "../../page.module.css";

export default function CorePage() {
  const [result, setResult] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);

  async function createIdentity(e: FormEvent<HTMLFormElement>) {
    e.preventDefault();
    const form = new FormData(e.currentTarget);
    setBusy(true);
    setError(null);
    try {
      const identity = await apiPost<Record<string, unknown>>("/v1/core/identities", {
        userName: String(form.get("userName")),
        email: String(form.get("email")),
      });
      setResult(JSON.stringify(identity, null, 2));
    } catch (err) {
      setError(err instanceof Error ? err.message : "Create failed");
    } finally {
      setBusy(false);
    }
  }

  async function createRole(e: FormEvent<HTMLFormElement>) {
    e.preventDefault();
    const form = new FormData(e.currentTarget);
    setBusy(true);
    setError(null);
    try {
      const role = await apiPost<Record<string, unknown>>("/v1/core/roles", {
        name: String(form.get("name")),
        description: String(form.get("description")),
      });
      setResult(JSON.stringify(role, null, 2));
    } catch (err) {
      setError(err instanceof Error ? err.message : "Create failed");
    } finally {
      setBusy(false);
    }
  }

  return (
    <OpsShell
      title="Identity core"
      lead="V2 Bootstrap IAM — create identities and roles for joiner / access-request flows."
      activeHref="/ops/core"
    >
      {error ? (
        <div className={styles.errorBanner} role="alert">
          {error}
        </div>
      ) : null}

      <div className={styles.panel}>
        <h2 className={styles.panelTitle}>Create identity</h2>
        <form onSubmit={createIdentity}>
          <div className={styles.formGrid}>
            <div className={styles.field}>
              <label htmlFor="userName">Username</label>
              <input id="userName" name="userName" defaultValue="core.user" required />
            </div>
            <div className={styles.field}>
              <label htmlFor="email">Email</label>
              <input
                id="email"
                name="email"
                type="email"
                defaultValue="core.user@example.com"
                required
              />
            </div>
          </div>
          <div className={styles.formActions}>
            <button className={styles.primary} type="submit" disabled={busy}>
              Create identity
            </button>
          </div>
        </form>
      </div>

      <div className={styles.panel}>
        <h2 className={styles.panelTitle}>Create role</h2>
        <form onSubmit={createRole}>
          <div className={styles.formGrid}>
            <div className={styles.field}>
              <label htmlFor="name">Name</label>
              <input id="name" name="name" defaultValue="app-reader" required />
            </div>
            <div className={styles.field}>
              <label htmlFor="description">Description</label>
              <input id="description" name="description" defaultValue="Read-only application access" />
            </div>
          </div>
          <div className={styles.formActions}>
            <button className={styles.secondary} type="submit" disabled={busy}>
              Create role
            </button>
          </div>
        </form>
      </div>

      {result ? <pre className={styles.resultBox}>{result}</pre> : null}
    </OpsShell>
  );
}
