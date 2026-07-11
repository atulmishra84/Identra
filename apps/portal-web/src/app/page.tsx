import { FabricMesh } from "../components/FabricMesh";
import styles from "./page.module.css";

export default function HomePage() {
  return (
    <div className={styles.page}>
      <header className={styles.topbar}>
        <a className={styles.logoMark} href="/" aria-label="Identra home">
          <span className={styles.logoGlyph} aria-hidden="true" />
          Identra
        </a>
        <nav className={styles.nav} aria-label="Primary">
          <a href="#platform">Platform</a>
          <a href="#releases">V1–V3</a>
          <a href="/ops">Console</a>
          <a className={styles.navCta} href="/ops">
            Open console
          </a>
        </nav>
      </header>

      <main>
        <section className={styles.hero} aria-labelledby="hero-brand">
          <div className={styles.heroCopy}>
            <p id="hero-brand" className={styles.brand}>
              Identra
            </p>
            <h1 className={styles.headline}>
              Identity Fabric for every IAM
            </h1>
            <p className={styles.lead}>
              Applications speak Identra. Identra speaks SailPoint, Okta, and the
              rest — so vendor migrations change adapters, not your apps.
            </p>
            <div className={styles.actions}>
              <a className={styles.primary} href="/ops">
                Platform console
              </a>
              <a className={styles.secondary} href="#releases">
                Explore V2 &amp; V3
              </a>
            </div>
          </div>
          <div className={styles.heroVisual} aria-hidden="true">
            <FabricMesh />
          </div>
        </section>

        <section id="platform" className={styles.platform}>
          <p className={styles.sectionEyebrow}>Platform</p>
          <h2 className={styles.sectionTitle}>
            One control plane. Swap the fabric underneath.
          </h2>
          <p className={styles.sectionLead}>
            Identra sits between your applications and every identity provider —
            normalizing SCIM, entitlements, and lifecycle so you never rewrite
            integrations when the vendor changes.
          </p>
          <ol className={styles.steps}>
            <li>
              <span className={styles.stepIndex}>01</span>
              <div>
                <strong>Apps integrate once</strong>
                <p>Stable Identra APIs for identity, access, and governance.</p>
              </div>
            </li>
            <li>
              <span className={styles.stepIndex}>02</span>
              <div>
                <strong>Adapters speak vendors</strong>
                <p>Okta, SailPoint ISC, Entra, Saviynt, Ping, and more.</p>
              </div>
            </li>
            <li>
              <span className={styles.stepIndex}>03</span>
              <div>
                <strong>Swap without rewrites</strong>
                <p>Change the adapter target — keep every app on Identra.</p>
              </div>
            </li>
          </ol>
        </section>

        <section id="releases" className={styles.versionSection}>
          <p className={styles.sectionEyebrow}>Releases</p>
          <h2 className={styles.sectionTitle}>V1 Fabric. V2 Platform. V3 Scale.</h2>
          <p className={styles.sectionLead}>
            Every release is live in the console — adapters, AI factories,
            marketplace, migrations, automation, and certifications.
          </p>
          <div className={styles.versionGrid}>
            <div className={styles.versionBlock}>
              <h3>V1 — Identity Fabric</h3>
              <ul>
                <li>Vendor-neutral APIs &amp; SCIM</li>
                <li>Adapter catalog &amp; health</li>
                <li>Swap-compatible routing</li>
              </ul>
              <a href="/ops/adapters">Open adapters →</a>
            </div>
            <div className={styles.versionBlock}>
              <h3>V2 — AI Platform</h3>
              <ul>
                <li>Connector factory &amp; AI gateway</li>
                <li>Governance scorecards</li>
                <li>Migrations, marketplace, Identity Core</li>
              </ul>
              <a href="/ops/marketplace">Open marketplace →</a>
            </div>
            <div className={styles.versionBlock}>
              <h3>V3 — Automation &amp; Cert</h3>
              <ul>
                <li>Automation factory (Playwright+)</li>
                <li>Access certification campaigns</li>
                <li>SoD evaluation &amp; full vendor catalog</li>
              </ul>
              <a href="/ops/automation">Open automation →</a>
            </div>
          </div>
        </section>
      </main>

      <footer className={styles.footer}>
        <span className={styles.footerBrand}>Identra</span>
        <span className={styles.footerMeta}>Identity Fabric Platform · V1–V3</span>
        <a href="/ops">Platform console →</a>
      </footer>
    </div>
  );
}
