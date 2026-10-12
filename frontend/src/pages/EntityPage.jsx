import { useEffect, useState } from "react";
import { api, currentUser } from "../api.js";

const FIELDS = {
  cars: [
    ["vin", "VIN (17 chars)", "text"],
    ["brand", "Brand", "text"],
    ["model", "Model", "text"],
    ["manufactureYear", "Year", "number"],
    ["price", "Price (RWF)", "number"],
    ["status", "Status", "select", ["AVAILABLE", "RESERVED", "SOLD"]],
    ["imageFile", "Photo file (e.g. landcruiser.jpg)", "text"],
  ],
  customers: [
    ["firstName", "First name", "text"],
    ["lastName", "Last name", "text"],
    ["email", "Email", "email"],
    ["phoneNumber", "Phone", "text"],
    ["creditLimit", "Credit limit (RWF)", "number"],
  ],
  employees: [
    ["firstName", "First name", "text"],
    ["lastName", "Last name", "text"],
    ["email", "Email", "email"],
    ["phoneNumber", "Phone", "text"],
    ["employeeNumber", "Employee number (EMP-0001)", "text"],
    ["hireDate", "Hire date", "date"],
    ["role", "Role", "select", ["SALES_AGENT", "MANAGER", "ADMIN"]],
    ["salary", "Salary (RWF)", "number"],
    ["portalPassword", "Portal password (min 8, required for new staff)", "password"],
  ],
};

const PATH = { cars: "car", customers: "customer", employees: "employee" };
const TITLE = { cars: "Inventory", customers: "Customers", employees: "Staff" };
const NUMBERS = ["manufactureYear", "price", "creditLimit", "salary"];

export default function EntityPage({ entity }) {
  const user = currentUser();
  const [rows, setRows] = useState([]);
  const [editing, setEditing] = useState(null);
  const [error, setError] = useState("");
  const canWrite = entity === "employees"
    ? user?.role === "ADMIN"
    : ["ADMIN", "MANAGER", "SALES_AGENT"].includes(user?.role);

  async function load() {
    setRows(await api(`/${PATH[entity]}/search-all`));
  }

  useEffect(() => {
    setEditing(null);
    setError("");
    load().catch((err) => setError(err.message));
  }, [entity]);

  async function save(e) {
    e.preventDefault();
    const body = Object.fromEntries(new FormData(e.target).entries());
    NUMBERS.forEach((k) => { if (body[k] !== undefined) body[k] = Number(body[k]); });
    if (!body.portalPassword) delete body.portalPassword;
    if (editing?.id) body.id = editing.id;
    try {
      await api(editing?.id ? `/${PATH[entity]}/update` : `/${PATH[entity]}/create`, {
        method: editing?.id ? "PUT" : "POST",
        body: JSON.stringify(body),
      });
      setEditing(null);
      setError("");
      await load();
    } catch (err) {
      setError(err.message);
    }
  }

  async function remove(id) {
    if (!confirm("Delete this record?")) return;
    try {
      await api(`/${PATH[entity]}/delete/${id}`, { method: "DELETE" });
      await load();
    } catch (err) {
      alert(err.message);
    }
  }

  return (
    <>
      <div className="top">
        <div>
          <h2>{TITLE[entity]}</h2>
          <p className="lede">
            {entity === "employees"
              ? "Admin creates staff here. That also creates their portal login."
              : "Business rules stay in the Spring Boot service layer."}
          </p>
        </div>
        {canWrite && <button className="btn btn-primary" onClick={() => { setEditing({}); setError(""); }}>New</button>}
      </div>
      {error && <p className="banner show">{error}</p>}
      {editing && (
        <form className="panel form-grid" onSubmit={save}>
          {FIELDS[entity].map(([name, label, type, opts]) => (
            <label key={name}>{label}
              {type === "select" ? (
                <select name={name} defaultValue={editing[name] || opts[0]}>
                  {opts.map((o) => <option key={o}>{o}</option>)}
                </select>
              ) : (
                <input
                  name={name}
                  type={type}
                  defaultValue={name === "portalPassword" ? "" : (editing[name] ?? "")}
                  required={name !== "imageFile" && !(name === "portalPassword" && editing?.id)}
                />
              )}
            </label>
          ))}
          <div className="form-actions">
            <button type="button" className="btn btn-ghost" onClick={() => setEditing(null)}>Cancel</button>
            <button type="submit" className="btn btn-primary">Save</button>
          </div>
        </form>
      )}
      <div className="panel">
        <table>
          <thead>
            {entity === "cars" && <tr><th></th><th>VIN</th><th>Brand</th><th>Model</th><th>Year</th><th>Price</th><th>Status</th><th></th></tr>}
            {entity === "customers" && <tr><th>Name</th><th>Email</th><th>Phone</th><th>Credit</th><th></th></tr>}
            {entity === "employees" && <tr><th>No.</th><th>Name</th><th>Email</th><th>Role</th><th>Salary</th><th></th></tr>}
          </thead>
          <tbody>
            {rows.map((row) => (
              <tr key={row.id}>
                {entity === "cars" && <>
                  <td>{row.imageFile ? <img className="thumb" src={`/pics/${row.imageFile}`} alt="" /> : null}</td>
                  <td>{row.vin}</td><td>{row.brand}</td><td>{row.model}</td><td>{row.manufactureYear}</td>
                  <td>RWF {Number(row.price).toLocaleString("en-RW")}</td>
                  <td><span className={`badge ${row.status}`}>{row.status}</span></td>
                </>}
                {entity === "customers" && <>
                  <td>{row.firstName} {row.lastName}</td><td>{row.email}</td><td>{row.phoneNumber}</td>
                  <td>RWF {Number(row.creditLimit).toLocaleString("en-RW")}</td>
                </>}
                {entity === "employees" && <>
                  <td>{row.employeeNumber}</td><td>{row.firstName} {row.lastName}</td>
                  <td>{row.email}</td><td><span className={`badge ${row.role}`}>{row.role}</span></td>
                  <td>RWF {Number(row.salary).toLocaleString("en-RW")}</td>
                </>}
                <td className="actions">
                  {canWrite && <button className="btn btn-ghost small" onClick={() => setEditing(row)}>Edit</button>}
                  {canWrite && user?.role !== "SALES_AGENT" && (
                    <button className="btn btn-danger small" onClick={() => remove(row.id)}>Delete</button>
                  )}
                </td>
              </tr>
            ))}
            {rows.length === 0 && <tr><td colSpan="7" className="empty">Nothing here yet.</td></tr>}
          </tbody>
        </table>
      </div>
    </>
  );
}
