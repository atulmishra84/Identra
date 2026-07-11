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
          <a href="/ops">Ops</a>
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
                Ops console
              </a>
              <a className={styles.secondary} href="#platform">
                See how it works
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
      </main>

      <footer className={styles.footer}>
        <span className={styles.footerBrand}>Identra</span>
        <span className={styles.footerMeta}>Identity Fabric Platform</span>
        <a href="/ops">Ops console →</a>
      </footer>
    </div>
  );
}
