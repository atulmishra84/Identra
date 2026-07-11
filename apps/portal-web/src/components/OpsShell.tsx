import type { ReactNode } from "react";
import styles from "../app/page.module.css";

export type OpsNavItem = {
  href: string;
  label: string;
  version: "V1" | "V2" | "V3";
};

export const OPS_NAV: OpsNavItem[] = [
  { href: "/ops", label: "Overview", version: "V1" },
  { href: "/ops/adapters", label: "Adapters", version: "V1" },
  { href: "/ops/marketplace", label: "Marketplace", version: "V2" },
  { href: "/ops/connectors", label: "Connector factory", version: "V2" },
  { href: "/ops/migrations", label: "Migrations", version: "V2" },
  { href: "/ops/ai", label: "AI gateway", version: "V2" },
  { href: "/ops/governance", label: "Governance", version: "V2" },
  { href: "/ops/core", label: "Identity core", version: "V2" },
  { href: "/ops/automation", label: "Automation", version: "V3" },
  { href: "/ops/certifications", label: "Certifications", version: "V3" },
];

export function OpsShell({
  title,
  lead,
  activeHref,
  children,
}: {
  title: string;
  lead: string;
  activeHref: string;
  children: ReactNode;
}) {
  const groups: Array<"V1" | "V2" | "V3"> = ["V1", "V2", "V3"];

  return (
    <div className={styles.opsShell}>
      <header className={styles.opsHeader}>
        <a className={styles.opsBrand} href="/">
          <span className={styles.logoGlyph} aria-hidden="true" />
          Identra
        </a>
        <nav className={styles.opsNav} aria-label="Ops">
          <a href="/">Home</a>
          <span aria-current="page">Platform console</span>
        </nav>
      </header>

      <div className={styles.opsLayout}>
        <aside className={styles.opsAside} aria-label="Modules">
          {groups.map((version) => (
            <div key={version} className={styles.navGroup}>
              <p className={styles.navGroupLabel}>{version}</p>
              <ul className={styles.navList}>
                {OPS_NAV.filter((item) => item.version === version).map((item) => {
                  const active = item.href === activeHref;
                  return (
                    <li key={item.href}>
                      <a
                        href={item.href}
                        className={active ? styles.navLinkActive : styles.navLink}
                        aria-current={active ? "page" : undefined}
                      >
                        {item.label}
                      </a>
                    </li>
                  );
                })}
              </ul>
            </div>
          ))}
        </aside>

        <main className={styles.opsMain}>
          <h1 className={styles.opsTitle}>{title}</h1>
          <p className={styles.opsLead}>{lead}</p>
          {children}
        </main>
      </div>
    </div>
  );
}
