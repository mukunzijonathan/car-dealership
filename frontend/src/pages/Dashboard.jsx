import { useEffect, useState } from "react";
import { api, currentUser } from "../api.js";
import { carPhoto } from "../CarCard.jsx";

export default function Dashboard() {
  const role = currentUser()?.role;
  const [data, setData] = useState({ cars: [], customers: [], employees: [], sales: [], pending: [] });
  const [error, setError] = useState("");

  useEffect(() => {
    Promise.all([
      api("/car/search-all").catch(() => []),
      api("/customer/search-all").catch(() => []),
      role === "ADMIN" ? api("/employee/search-all").catch(() => []) : Promise.resolve([]),
      api("/sale/search-all").catch(() => []),
      api("/sale/pending").catch(() => []),
    ])
      .then(([cars, customers, employees, sales, pending]) => {
        setData({ cars, customers, employees, sales, pending });
      })
      .catch((err) => setError(err.message));
  }, [role]);

  const available = data.cars.filter((c) => c.status === "AVAILABLE").length;
  const approved = data.sales.filter((s) => s.status === "APPROVED").length;

  return (
    <>
      <div className="top">
        <div>
          <h2>{role === "ADMIN" ? "Admin dashboard" : role === "MANAGER" ? "Manager dashboard" : "Sales desk"}</h2>
          <p className="lede">
            {role === "ADMIN" && "Users, stock, staff and every sale request."}
            {role === "MANAGER" && "Stock, customers and sale approvals. Staff accounts stay with Admin."}
            {role === "SALES_AGENT" && "Accept customer requests. You do not manage staff or audit logs."}
          </p>
        </div>
      </div>
      {error && <p className="banner show">{error}</p>}
      <div className="stats">
        <div className="stat"><span>Available cars</span><strong>{available}</strong></div>
        <div className="stat"><span>Pending requests</span><strong>{data.pending.length}</strong></div>
        {(role === "ADMIN" || role === "MANAGER") && (
          <div className="stat"><span>Customers</span><strong>{data.customers.length}</strong></div>
        )}
        {role === "ADMIN" && (
          <div className="stat"><span>Staff</span><strong>{data.employees.length}</strong></div>
        )}
        {role !== "ADMIN" && (
          <div className="stat"><span>Approved sales</span><strong>{approved}</strong></div>
        )}
      </div>
      {data.pending.length > 0 && (
        <>
          <h3 className="section-title">Waiting for approval</h3>
          <div className="panel">
            <table>
              <thead><tr><th>Car</th><th>Customer</th><th>Amount</th><th>Status</th></tr></thead>
              <tbody>
                {data.pending.map((s) => (
                  <tr key={s.id}>
                    <td>{s.car?.brand} {s.car?.model}</td>
                    <td>{s.customer?.firstName} {s.customer?.lastName}</td>
                    <td>RWF {Number(s.amount).toLocaleString("en-RW")}</td>
                    <td><span className={`badge ${s.status}`}>{s.status}</span></td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </>
      )}
      <h3 className="section-title">Inventory</h3>
      <div className="panel">
        <table>
          <thead><tr><th></th><th>Brand</th><th>Model</th><th>Year</th><th>Price</th><th>Status</th></tr></thead>
          <tbody>
            {data.cars.map((c) => (
              <tr key={c.id}>
                <td>{carPhoto(c) ? <img className="thumb" src={carPhoto(c)} alt="" /> : null}</td>
                <td>{c.brand}</td><td>{c.model}</td><td>{c.manufactureYear}</td>
                <td>RWF {Number(c.price).toLocaleString("en-RW")}</td>
                <td><span className={`badge ${c.status}`}>{c.status}</span></td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </>
  );
}
