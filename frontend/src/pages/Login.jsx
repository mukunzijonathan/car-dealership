import { useEffect, useState } from "react";
import { Link, Navigate, useNavigate } from "react-router-dom";
import { api, currentUser, setSession } from "../api.js";
import { homePath } from "../Logo.jsx";
import PublicHeader from "../PublicHeader.jsx";

const DEMOS = [
  ["Admin", "admin@cdms.rw", "Admin#123"],
  ["Manager", "manager@cdms.rw", "Manager#123"],
  ["Agent", "agent@cdms.rw", "Agent#123"],
  ["Customer", "customer@cdms.rw", "Customer#123"],
];

export default function Login() {
  const nav = useNavigate();
  const existing = currentUser();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState("");
  const [googleEnabled, setGoogleEnabled] = useState(false);

  useEffect(() => {
    api("/auth/status").then((s) => setGoogleEnabled(Boolean(s.googleEnabled))).catch(() => {});
  }, []);

  if (existing) {
    return <Navigate to={homePath(existing)} replace />;
  }

  async function submit(e) {
    e.preventDefault();
    setError("");
    try {
      const auth = await api("/auth/login", {
        method: "POST",
        body: JSON.stringify({ email, password }),
      });
      setSession(auth);
      nav(homePath(auth), { replace: true });
    } catch (err) {
      setError(err.message);
    }
  }

  return (
    <div className="login-wrap">
      <PublicHeader />
      <div className="auth-split">
        <img className="auth-photo" src="/pics/porsche911.jpg" alt="Porsche on the showroom lot" />
        <form className="login-card" onSubmit={submit}>
          <h2>Sign in</h2>
          <p className="lede">Customers use the account they created. Staff use the login an admin created.</p>
          {error && <p className="banner show">{error}</p>}
          <label>Email
            <input value={email} onChange={(e) => setEmail(e.target.value)} type="email" autoComplete="username" required />
          </label>
          <label>Password
            <div className="password-field">
              <input value={password} onChange={(e) => setPassword(e.target.value)} type={showPassword ? "text" : "password"} autoComplete="current-password" required />
              <button type="button" className="eye-btn" onClick={() => setShowPassword((v) => !v)}>{showPassword ? "Hide" : "Show"}</button>
            </div>
          </label>
          <button className="btn btn-primary" type="submit">Sign in</button>
          {googleEnabled ? (
            <a className="btn btn-ghost" href="/oauth2/authorization/google">Continue with Google</a>
          ) : (
            <p className="note">Sign in with email and password, or continue with Google when OAuth 2.0 credentials are configured.</p>
          )}
          <Link className="btn btn-ghost" to="/register">Create a customer account</Link>
          <p className="note">Demo accounts — click one to fill the form:</p>
          <div className="demo-chip">
            {DEMOS.map(([label, e, p]) => (
              <button type="button" key={e} onClick={() => { setEmail(e); setPassword(p); }}>{label}</button>
            ))}
          </div>
        </form>
      </div>
    </div>
  );
}
