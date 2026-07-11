import { FabricMesh } from "../components/FabricMesh";
import styles from "./page.module.css";

export default function HomePage() {
  return (
    <div className={styles.page}>
      <header className={styles.topbar}>
        <a className={styles.logoMark} href="/" aria-label="Identra home">
          Identra
        </a>
        <nav className={styles.nav} aria-label="Primary">
          <a href="#platform">Platform</a>
          <a href="#releases">Releases</a>
          <a className={styles.navCta} href="/ops">
            Open console
          </a>
        </nav>
      </header>

      <main>
        <section className={styles.hero} aria-labelledby="hero-brand">
          <div className={styles.heroVisual} aria-hidden="true">
            <FabricMesh />
          </div>
          <div className={styles.heroCopy}>
            <p id="hero-brand" className={styles.brand}>
              Identra
            </p>
            <h1 className={styles.headline}>Identity Fabric for every IAM</h1>
            <p className={styles.lead}>
              Applications speak Identra. Identra speaks SailPoint, Okta, and the
              rest — so vendor migrations change adapters, not your apps.
            </p>
            <div className={styles.actions}>
              <a className={styles.primary} href="/ops">
                Open platform console
              </a>
              <a className={styles.secondary} href="#platform">
                How it works
              </a>
            </div>
          </div>
        </section>

        <section id="platform" className={styles.platform}>
          <p className={styles.sectionEyebrow}>Platform</p>
          <h2 className={styles.sectionTitle}>
            One control plane. Swap the fabric underneath.
          </h2>
          <p className={styles.sectionLead}>
            Identra sits between your applications and every identity provider,
            normalizing identity, access, and lifecycle so integrations survive
            vendor change.
          </p>
          <ol className={styles.steps}>
            <li>
              <span className={styles.stepIndex}>01</span>
              <div>
                <strong>Integrate once</strong>
                <p>Stable Identra APIs and SCIM for every application.</p>
              </div>
            </li>
            <li>
              <span className={styles.stepIndex}>02</span>
              <div>
                <strong>Adapt any IAM</strong>
                <p>Okta, SailPoint, Entra, Saviynt, Ping, and more.</p>
              </div>
            </li>
            <li>
              <span className={styles.stepIndex}>03</span>
              <div>
                <strong>Swap without rewrites</strong>
                <p>Change adapters — keep every app on Identra.</p>
              </div>
            </li>
          </ol>
        </section>

        <section id="releases" className={styles.versionSection}>
          <p className={styles.sectionEyebrow}>Releases</p>
          <h2 className={styles.sectionTitle}>Built through V1, V2, and V3</h2>
          <p className={styles.sectionLead}>
            Fabric, AI platform, and automation — available in the console now.
          </p>
          <div className={styles.versionGrid}>
            <a className={styles.versionBlock} href="/ops/adapters">
              <span className={styles.moduleVersion}>V1</span>
              <h3>Identity Fabric</h3>
              <p>Adapters, SCIM, and swap-compatible routing.</p>
            </a>
            <a className={styles.versionBlock} href="/ops/marketplace">
              <span className={styles.moduleVersion}>V2</span>
              <h3>AI Platform</h3>
              <p>Connector factory, governance, migrations, marketplace.</p>
            </a>
            <a className={styles.versionBlock} href="/ops/automation">
              <span className={styles.moduleVersion}>V3</span>
              <h3>Automation &amp; Cert</h3>
              <p>Automation packs, certifications, and SoD evaluation.</p>
            </a>
          </div>
        </section>
      </main>

      <footer className={styles.footer}>
        <span className={styles.footerBrand}>Identra</span>
        <span className={styles.footerMeta}>Identity Fabric Platform</span>
        <a href="/ops">Console</a>
      </footer>
    </div>
  );
}
