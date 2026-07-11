import type { CSSProperties } from "react";
import type { Metadata } from "next";
import { Instrument_Serif, Public_Sans } from "next/font/google";
import "./globals.css";

const instrument = Instrument_Serif({
  subsets: ["latin"],
  weight: "400",
  variable: "--font-instrument",
  display: "swap",
});

const publicSans = Public_Sans({
  subsets: ["latin"],
  variable: "--font-public",
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
    <html lang="en" className={`${instrument.variable} ${publicSans.variable}`}>
      <body
        style={
          {
            "--font-display": "var(--font-instrument), Instrument Serif, Georgia, serif",
            "--font-body": "var(--font-public), Public Sans, sans-serif",
          } as CSSProperties
        }
      >
        {children}
      </body>
    </html>
  );
}
