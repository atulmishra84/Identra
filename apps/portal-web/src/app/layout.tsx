import type { CSSProperties } from "react";
import type { Metadata } from "next";
import { Figtree, Syne } from "next/font/google";
import "./globals.css";

const syne = Syne({
  subsets: ["latin"],
  variable: "--font-syne",
  display: "swap",
});

const figtree = Figtree({
  subsets: ["latin"],
  variable: "--font-figtree",
  display: "swap",
});

export const metadata: Metadata = {
  title: {
    default: "Identra — Identity Fabric",
    template: "%s · Identra",
  },
  description:
    "Vendor-neutral Identity Fabric. Applications speak Identra; Identra speaks every IAM.",
  icons: {
    icon: "/favicon.svg",
  },
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en" className={`${syne.variable} ${figtree.variable}`}>
      <body
        style={
          {
            "--font-display": "var(--font-syne), Syne, sans-serif",
            "--font-body": "var(--font-figtree), Figtree, sans-serif",
          } as CSSProperties
        }
      >
        {children}
      </body>
    </html>
  );
}
