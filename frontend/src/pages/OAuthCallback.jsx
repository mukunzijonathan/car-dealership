import { useEffect } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import { api, setSession } from "../api.js";

export default function OAuthCallback() {
  const [params] = useSearchParams();
  const nav = useNavigate();

  useEffect(() => {
    const token = params.get("token");
    if (!token) {
      nav("/login", { replace: true });
      return;
    }
    localStorage.setItem("cdms_token", token);
    api("/auth/me")
      .then((me) => {
        setSession({ ...me, token });
        const dest = me.role === "CUSTOMER" ? "/app/showroom" : "/app";
        nav(dest, { replace: true });
      })
      .catch(() => nav("/login", { replace: true }));
  }, [params, nav]);

  return <p className="lede">Signing you in…</p>;
}
