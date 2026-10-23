export const API_MODE = import.meta.env.VITE_API_MODE === 'true';
const API_BASE = (import.meta.env.VITE_API_URL || '/api').replace(/\/$/, '');
const ACCESS_KEY = 'asl-api-access';
const REFRESH_KEY = 'asl-api-refresh';

export function getApiAccess() { try { return localStorage.getItem(ACCESS_KEY); } catch { return null; } }
export function getApiRefresh() { try { return localStorage.getItem(REFRESH_KEY); } catch { return null; } }
export function saveApiTokens(access, refresh) {
  try { localStorage.setItem(ACCESS_KEY, access); localStorage.setItem(REFRESH_KEY, refresh); } catch { /* private browsing */ }
}
export function clearApiTokens() {
  try { localStorage.removeItem(ACCESS_KEY); localStorage.removeItem(REFRESH_KEY); } catch { /* private browsing */ }
}

export async function apiRequest(path, { method = 'GET', body, headers = {}, auth = true } = {}) {
  const token = auth ? getApiAccess() : null;
  const response = await fetch(`${API_BASE}${path.startsWith('/') ? path : `/${path}`}`, {
    method,
    headers: { ...(body ? { 'Content-Type': 'application/json' } : {}), ...(token ? { Authorization: `Bearer ${token}` } : {}), ...headers },
    body: body ? JSON.stringify(body) : undefined,
  });
  if (response.status === 204) return null;
  const payload = await response.json().catch(() => ({}));
  if (!response.ok) {
    const message = payload.detail || payload.message || payload.title || `Request failed (${response.status})`;
    const error = new Error(message);
    error.status = response.status;
    error.payload = payload;
    throw error;
  }
  return payload;
}
