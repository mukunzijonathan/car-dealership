import { useEffect, useMemo, useState } from "react";
import { api } from "../api.js";
import CarCard, { CatalogToolbar, money } from "../CarCard.jsx";
import { brandsOf, filterCars } from "../catalog.js";

export default function Showroom() {
  const [cars, setCars] = useState([]);
  const [mine, setMine] = useState([]);
  const [query, setQuery] = useState("");
  const [brand, setBrand] = useState("All");
  const [band, setBand] = useState("all");
  const [sort, setSort] = useState("featured");
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");

  async function load() {
    setError("");
    try {
      setCars((await api("/car/search-lot")).filter((c) => c.imageFile));
    } catch (err) {
      try {
        setCars(await api("/car/search-available"));
      } catch (inner) {
        setError(inner.message);
      }
    }
    try {
      setMine(await api("/sale/mine"));
    } catch (err) {
      setError(err.message);
    }
  }

  useEffect(() => { load(); }, []);

  const brands = useMemo(() => brandsOf(cars), [cars]);
  const visible = useMemo(
    () => filterCars(cars, { query, brand, band, sort }),
    [cars, query, brand, band, sort]
  );

  async function requestCar(car) {
    setError("");
    setMessage("");
    try {
      await api("/sale/request", {
        method: "POST",
        body: JSON.stringify({ car: { id: car.id } }),
      });
      setMessage(`Request sent for the ${car.brand} ${car.model}. A sales agent will review it.`);
      await load();
    } catch (err) {
      setError(err.message);
    }
  }

  return (
    <>
      <div className="top">
        <div>
          <h2>Showroom</h2>
          <p className="lede">Request one available car. Reserved and sold cars stay on the lot so you can see what left.</p>
        </div>
      </div>
      {error && <p className="banner show">{error}</p>}
      {message && <p className="banner show ok-banner">{message}</p>}
      <CatalogToolbar
        query={query}
        setQuery={setQuery}
        brand={brand}
        setBrand={setBrand}
        brands={brands}
        band={band}
        setBand={setBand}
        sort={sort}
        setSort={setSort}
      />
      <div className="car-grid">
        {visible.map((car) => (
          <CarCard
            key={car.id}
            car={car}
            action={car.status === "AVAILABLE"
              ? <button className="btn btn-primary small" type="button" onClick={() => requestCar(car)}>Request this car</button>
              : <p className="card-note">{car.status === "SOLD" ? "Already sold" : "Held for a customer"}</p>}
          />
        ))}
        {visible.length === 0 && <p className="empty">Nothing matches that search.</p>}
      </div>

      <h3 className="section-title">My requests</h3>
      <div className="panel">
        <table>
          <thead><tr><th>Car</th><th>Amount</th><th>Status</th></tr></thead>
          <tbody>
            {mine.map((s) => (
              <tr key={s.id}>
                <td>{s.car?.brand} {s.car?.model}</td>
                <td>{money(s.amount)}</td>
                <td><span className={`badge ${s.status}`}>{s.status}</span></td>
              </tr>
            ))}
            {mine.length === 0 && <tr><td colSpan="3" className="empty">You have not requested a car yet.</td></tr>}
          </tbody>
        </table>
      </div>
    </>
  );
}
