import { useEffect, useState } from "react";
import { api, currentUser } from "../api.js";

export default function Notifications() {
  const [notes, setNotes] = useState([]);
  const [audits, setAudits] = useState([]);
  const [error, setError] = useState("");
  const manager = ["ADMIN", "MANAGER"].includes(currentUser()?.role);

  useEffect(() => {
    api("/notification/search-all")
      .then(setNotes)
      .catch((err) => setError(err.message));
    if (manager) {
      api("/notification/audit/search-all").then(setAudits).catch(() => {});
    }
  }, [manager]);

  return (
    <>
      <div className="top">
        <div>
          <h2>Notifications</h2>
          <p className="lede">MongoDB documents written by the RabbitMQ email and SMS listeners.</p>
        </div>
      </div>
      {error && <p className="banner show">{error}</p>}
      <div className="panel">
        <table>
          <thead><tr><th>Channel</th><th>To</th><th>Subject</th><th>Status</th><th>When</th></tr></thead>
          <tbody>
            {notes.map((n) => (
              <tr key={n.id}>
                <td>{n.channel}</td><td>{n.recipient}</td><td>{n.subject}</td>
                <td>{n.status}</td><td>{n.createdAt?.replace("T", " ").slice(0, 19)}</td>
              </tr>
            ))}
            {notes.length === 0 && <tr><td colSpan="5" className="empty">No Mongo documents yet. Register a customer or accept a sale while Docker Mongo and RabbitMQ are up.</td></tr>}
          </tbody>
        </table>
      </div>
      {manager && (
        <div className="panel">
          <h3>Audit log</h3>
          <table>
            <thead><tr><th>Action</th><th>Actor</th><th>Details</th><th>When</th></tr></thead>
            <tbody>
              {audits.map((a) => (
                <tr key={a.id}>
                  <td>{a.action}</td><td>{a.actorEmail}</td><td>{a.details}</td>
                  <td>{a.createdAt?.replace("T", " ").slice(0, 19)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </>
  );
}
