import styles from "./page.module.css";

export default function HomePage() {
  return (
    <main className={styles.hero}>
      <p className={styles.brand}>Identra</p>
      <h1 className={styles.title}>Identity Fabric for every IAM</h1>
      <p className={styles.lead}>
        Applications speak Identra. Identra speaks SailPoint, Okta, and the rest —
        so vendor migrations change adapters, not your apps.
      </p>
      <div className={styles.actions}>
        <a className={styles.primary} href="/docs">
          Architecture
        </a>
        <a className={styles.secondary} href="https://github.com">
          OpenAPI
        </a>
      </div>
    </main>
  );
}
