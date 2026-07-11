/** @type {import('next').NextConfig} */
const nextConfig = {
  reactStrictMode: true,
  output: "standalone",
  async rewrites() {
    const gateway = process.env.IDENTRA_GATEWAY_URL || "http://localhost:8080";
    return [
      {
        source: "/identra/:path*",
        destination: `${gateway}/:path*`,
      },
    ];
  },
};

module.exports = nextConfig;
