import { useState } from "react";
import { Link, Navigate, useNavigate } from "react-router-dom";
import { api, currentUser, setSession } from "../api.js";
import PublicHeader from "../PublicHeader.jsx";

export default function Register() {
  const nav = useNavigate();
  const existing = currentUser();
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState("");

  if (existing) {
    return <Navigate to="/app/showroom" replace />;
  }

  async function submit(e) {
    e.preventDefault();
    setError("");
    const body = Object.fromEntries(new FormData(e.target).entries());
    try {
      const auth = await api("/auth/register", {
        method: "POST",
        body: JSON.stringify(body),
      });
      setSession(auth);
      nav("/app/showroom", { replace: true });
    } catch (err) {
      setError(err.message);
    }
  }

  return (
    <div className="login-wrap">
      <PublicHeader />
      <div className="auth-split">
        <img className="auth-photo" src="/pics/gle.jpg" alt="Mercedes GLE on the showroom lot" />
        <form className="login-card" onSubmit={submit}>
          <h2>Create a customer account</h2>
          <p className="lede">Use this if you want to request a car. Staff accounts are created only by an admin.</p>
          {error && <p className="banner show">{error}</p>}
          <label>First name<input name="firstName" required /></label>
          <label>Last name<input name="lastName" required /></label>
          <label>Email<input name="email" type="email" required /></label>
          <label>Phone<input name="phoneNumber" required placeholder="+2507…" /></label>
          <label>Password (min 8 characters)
            <div className="password-field">
              <input name="password" type={showPassword ? "text" : "password"} minLength={8} required />
              <button type="button" className="eye-btn" onClick={() => setShowPassword((v) => !v)}>{showPassword ? "Hide" : "Show"}</button>
            </div>
          </label>
          <button className="btn btn-primary" type="submit">Create account</button>
          <p className="note">A welcome notification is stored after registration. Staff accounts are created only by an administrator.</p>
          <Link className="btn btn-ghost" to="/login">Already have an account? Sign in</Link>
        </form>
      </div>
    </div>
  );
}
