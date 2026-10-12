const TOKEN_KEY = "cdms_token";

export function getToken() {
  return localStorage.getItem(TOKEN_KEY);
}

export function setSession(auth) {
  localStorage.setItem(TOKEN_KEY, auth.token);
  localStorage.setItem("cdms_user", JSON.stringify(auth));
}

export function clearSession() {
  localStorage.removeItem(TOKEN_KEY);
  localStorage.removeItem("cdms_user");
}

export function currentUser() {
  const raw = localStorage.getItem("cdms_user");
  return raw ? JSON.parse(raw) : null;
}

export async function api(path, options = {}) {
  const headers = { "Content-Type": "application/json", ...(options.headers || {}) };
  const token = getToken();
  if (token) headers.Authorization = `Bearer ${token}`;
  const res = await fetch(path, { ...options, headers });
  const data = await res.json().catch(() => ({}));
  if (!res.ok) {
    throw new Error(data.message || "Request failed");
  }
  return data;
}
