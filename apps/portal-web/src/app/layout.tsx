import type { Metadata } from "next";
import "./globals.css";

export const metadata: Metadata = {
  title: "Identra",
  description: "Vendor-neutral Identity Fabric Platform",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body>{children}</body>
    </html>
  );
}
