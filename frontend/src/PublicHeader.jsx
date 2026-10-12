import { Link } from "react-router-dom";
import Logo, { homePath } from "./Logo.jsx";
import { currentUser } from "./api.js";

export default function PublicHeader() {
  const user = currentUser();
  return (
    <header className="site-header">
      <Link to="/" className="brand brand-link">
        <Logo size={40} />
        <div>
          <h1>CDMS</h1>
          <p>Kigali showroom</p>
        </div>
      </Link>
      <nav className="site-nav">
        <a href="/#available">Inventory</a>
        <a href="/#how">How it works</a>
        {user ? (
          <Link className="btn btn-primary" to={homePath(user)}>Open portal</Link>
        ) : (
          <>
            <Link to="/login">Sign in</Link>
            <Link className="btn btn-primary" to="/register">Create account</Link>
          </>
        )}
      </nav>
    </header>
  );
}
