import { Link } from "react-router-dom";
import Logo from "./Logo.jsx";

export function carPhoto(car) {
  return car?.imageFile ? `/pics/${car.imageFile}` : null;
}

export function money(n) {
  return `RWF ${Number(n).toLocaleString("en-RW")}`;
}

export default function CarCard({ car, action, featured = false }) {
  const src = carPhoto(car);
  const vinTail = car.vin ? car.vin.slice(-6) : "";
  return (
    <article className={`car-card${featured ? " featured-card" : ""}`}>
      <div className="car-photo">
        {src ? <img src={src} alt={`${car.brand} ${car.model}`} /> : <Logo size={72} />}
        <span className={`badge ${car.status}`}>{car.status}</span>
      </div>
      <div className="car-body">
        <p className="car-brand">{car.brand}</p>
        <h4>{car.model}</h4>
        <p className="car-meta">{car.manufactureYear} · VIN …{vinTail}</p>
        <p className="car-price">{money(car.price)}</p>
        {action}
      </div>
    </article>
  );
}

export function RequestHint() {
  return <Link className="btn btn-ghost small" to="/register">Create account to request</Link>;
}

export function CatalogToolbar({ query, setQuery, brand, setBrand, brands, band, setBand, sort, setSort }) {
  return (
    <div className="catalog-bar">
      <label className="search">
        Search
        <input
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          placeholder="Brand, model or VIN"
        />
      </label>
      <div className="chips" role="tablist" aria-label="Brand">
        {brands.map((b) => (
          <button
            key={b}
            type="button"
            className={brand === b ? "chip on" : "chip"}
            onClick={() => setBrand(b)}
          >
            {b}
          </button>
        ))}
      </div>
      <div className="toolbar-selects">
        <label className="filter">
          Price
          <select value={band} onChange={(e) => setBand(e.target.value)}>
            <option value="all">Any price</option>
            <option value="under30">Under 30M</option>
            <option value="mid">30M – 70M</option>
            <option value="over70">70M and above</option>
          </select>
        </label>
        <label className="filter">
          Sort
          <select value={sort} onChange={(e) => setSort(e.target.value)}>
            <option value="featured">Featured</option>
            <option value="price-asc">Price: low to high</option>
            <option value="price-desc">Price: high to low</option>
            <option value="year-desc">Newest year</option>
          </select>
        </label>
      </div>
    </div>
  );
}
