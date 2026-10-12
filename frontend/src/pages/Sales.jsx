import { useEffect, useState } from "react";
import { api, currentUser } from "../api.js";

export default function Sales() {
  const role = currentUser()?.role;
  const [sales, setSales] = useState([]);
  const [pending, setPending] = useState([]);
  const [error, setError] = useState("");

  async function load() {
    const [all, waiting] = await Promise.all([
      api("/sale/search-all"),
      api("/sale/pending"),
    ]);
    setSales(all);
    setPending(waiting);
  }

  useEffect(() => {
    load().catch((err) => setError(err.message));
  }, []);

  async function decide(id, action) {
    setError("");
    try {
      await api(`/sale/${action}/${id}`, { method: "POST" });
      await load();
    } catch (err) {
      setError(err.message);
    }
  }

  return (
    <>
      <div className="top">
        <div>
          <h2>{role === "SALES_AGENT" ? "Sale requests" : "Sales"}</h2>
          <p className="lede">Customers request a car. You accept it to mark the car SOLD, or reject it to put it back on the lot.</p>
        </div>
      </div>
      {error && <p className="banner show">{error}</p>}

      <h3 className="section-title">Waiting for approval</h3>
      <div className="panel">
        <table>
          <thead><tr><th>Car</th><th>Customer</th><th>Amount</th><th></th></tr></thead>
          <tbody>
            {pending.map((s) => (
              <tr key={s.id}>
                <td>{s.car?.brand} {s.car?.model}</td>
                <td>{s.customer?.firstName} {s.customer?.lastName}</td>
                <td>RWF {Number(s.amount).toLocaleString("en-RW")}</td>
                <td className="actions">
                  <button className="btn btn-primary small" type="button" onClick={() => decide(s.id, "approve")}>Accept</button>
                  <button className="btn btn-danger small" type="button" onClick={() => decide(s.id, "reject")}>Reject</button>
                </td>
              </tr>
            ))}
            {pending.length === 0 && <tr><td colSpan="4" className="empty">No pending requests.</td></tr>}
          </tbody>
        </table>
      </div>

      {role !== "SALES_AGENT" && (
        <>
          <h3 className="section-title">All sales</h3>
          <div className="panel">
            <table>
              <thead><tr><th>Date</th><th>Car</th><th>Customer</th><th>Agent</th><th>Amount</th><th>Status</th></tr></thead>
              <tbody>
                {sales.map((s) => (
                  <tr key={s.id}>
                    <td>{s.soldOn || "—"}</td>
                    <td>{s.car?.brand} {s.car?.model}</td>
                    <td>{s.customer?.firstName} {s.customer?.lastName}</td>
                    <td>{s.employee ? `${s.employee.firstName} ${s.employee.lastName}` : "—"}</td>
                    <td>RWF {Number(s.amount).toLocaleString("en-RW")}</td>
                    <td><span className={`badge ${s.status}`}>{s.status}</span></td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </>
      )}
    </>
  );
}
