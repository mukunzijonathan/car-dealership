import { useEffect, useMemo, useState } from "react";
import { Link } from "react-router-dom";
import { api, currentUser } from "../api.js";
import { homePath } from "../Logo.jsx";
import CarCard, { CatalogToolbar, RequestHint } from "../CarCard.jsx";
import PublicHeader from "../PublicHeader.jsx";
import { brandsOf, featuredCars, filterCars } from "../catalog.js";

export default function Landing() {
  const [cars, setCars] = useState([]);
  const [query, setQuery] = useState("");
  const [brand, setBrand] = useState("All");
  const [band, setBand] = useState("all");
  const [sort, setSort] = useState("featured");
  const user = currentUser();

  useEffect(() => {
    api("/car/search-lot")
      .then((list) => setCars(list.filter((c) => c.imageFile)))
      .catch(() => api("/car/search-available").then((list) => setCars(list.filter((c) => c.imageFile))).catch(() => setCars([])));
  }, []);

  const brands = useMemo(() => brandsOf(cars), [cars]);
  const visible = useMemo(
    () => filterCars(cars, { query, brand, band, sort }),
    [cars, query, brand, band, sort]
  );
  const featured = useMemo(() => featuredCars(cars), [cars]);
  const available = cars.filter((c) => c.status === "AVAILABLE").length;

  function actionFor(car) {
    if (car.status !== "AVAILABLE") {
      return <p className="card-note">{car.status === "SOLD" ? "Already sold" : "Held for a customer"}</p>;
    }
    if (user?.role === "CUSTOMER") {
      return <Link className="btn btn-primary small" to="/app/showroom">Request in portal</Link>;
    }
    if (!user) return <RequestHint />;
    return <Link className="btn btn-ghost small" to={homePath(user)}>Open staff portal</Link>;
  }

  return (
    <div className="site">
      <PublicHeader />

      <section className="hero-band">
        <div className="hero-copy">
          <p className="eyebrow">Kigali showroom · KN 5 Ave</p>
          <h2>Find the next car on the lot.</h2>
          <p className="lede">
            Browse the Kigali lot without an account. Register as a customer when you
            want to request a car. A sales agent accepts the sale.
          </p>
          <form className="hero-search" onSubmit={(e) => { e.preventDefault(); document.getElementById("available")?.scrollIntoView({ behavior: "smooth" }); }}>
            <input
              value={query}
              onChange={(e) => setQuery(e.target.value)}
              placeholder="Search Land Cruiser, BMW, VIN…"
            />
            <button className="btn btn-primary" type="submit">Search the lot</button>
          </form>
          <div className="hero-actions">
            <a className="btn btn-light" href="#available">View all cars</a>
            {user ? (
              <Link className="btn btn-ghost-light" to={homePath(user)}>Go to your portal</Link>
            ) : (
              <Link className="btn btn-ghost-light" to="/register">Create a customer account</Link>
            )}
          </div>
        </div>
        <img className="hero-photo" src="/pics/gle.jpg" alt="Mercedes GLE on the Kigali lot" />
      </section>

      <section className="strip">
        <div><strong>{cars.length}</strong><span>cars in inventory</span></div>
        <div><strong>{available}</strong><span>available now</span></div>
        <div><strong>{Math.max(brands.length - 1, 0)}</strong><span>brands</span></div>
        <div><strong>Kigali</strong><span>KN 5 Ave showroom</span></div>
      </section>

      {featured.length > 0 && (
        <section className="section" id="featured">
          <div className="section-head">
            <div>
              <h3>Featured on the lot</h3>
              <p className="lede">Highest-value cars that are still available.</p>
            </div>
          </div>
          <div className="car-grid featured-grid">
            {featured.map((car) => (
              <CarCard key={car.id} car={car} featured action={actionFor(car)} />
            ))}
          </div>
        </section>
      )}

      <section className="section" id="available">
        <div className="section-head">
          <div>
            <h3>Full inventory</h3>
            <p className="lede">{visible.length} cars match your filters. Sold and reserved cars stay visible so the lot looks complete.</p>
          </div>
        </div>
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
            <CarCard key={car.id} car={car} action={actionFor(car)} />
          ))}
          {visible.length === 0 && <p className="empty">No cars match that search. Clear the filters and try again.</p>}
        </div>
      </section>

      <section className="section" id="how">
        <h3>How a sale works</h3>
        <div className="steps">
          <article><span>1</span><h4>Browse</h4><p>Anyone can see the lot, including reserved and sold cars. No login required.</p></article>
          <article><span>2</span><h4>Request</h4><p>A customer creates an account and requests one available car. It becomes reserved.</p></article>
          <article><span>3</span><h4>Accept</h4><p>A sales agent accepts. The car is marked sold and a notification is stored in MongoDB.</p></article>
        </div>
      </section>

      <footer className="site-footer">
        <div>
          <p>CDMS · Car Dealership Management System · Kigali</p>
          <p>Staff logins are created by an admin. Customers register themselves.</p>
        </div>
        <p className="footer-hint">Demo: admin@cdms.rw / Admin#123 · customer@cdms.rw / Customer#123</p>
      </footer>
    </div>
  );
}
