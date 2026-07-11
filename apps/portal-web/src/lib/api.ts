const TENANT = "11111111-1111-1111-1111-111111111111";
export const GATEWAY = process.env.NEXT_PUBLIC_IDENTRA_GATEWAY_URL ?? "/identra";

export function tenantHeaders(extra?: HeadersInit): HeadersInit {
  return {
    "X-Tenant-Id": TENANT,
    Accept: "application/json",
    ...extra,
  };
}

export async function apiGet<T>(path: string): Promise<T> {
  const res = await fetch(`${GATEWAY}${path}`, { headers: tenantHeaders() });
  if (!res.ok) {
    throw new Error(`Gateway returned ${res.status} for ${path}`);
  }
  return res.json() as Promise<T>;
}

export async function apiPost<T>(path: string, body: unknown): Promise<T> {
  const res = await fetch(`${GATEWAY}${path}`, {
    method: "POST",
    headers: tenantHeaders({ "Content-Type": "application/json" }),
    body: JSON.stringify(body),
  });
  if (!res.ok) {
    const text = await res.text().catch(() => "");
    throw new Error(text || `Gateway returned ${res.status} for ${path}`);
  }
  return res.json() as Promise<T>;
}
