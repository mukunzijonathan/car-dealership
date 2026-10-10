import { Link, Navigate, NavLink, Outlet, Route, Routes, useNavigate } from "react-router-dom";
import { clearSession, currentUser } from "./api.js";
import Logo from "./Logo.jsx";
import Login from "./pages/Login.jsx";
import Register from "./pages/Register.jsx";
import OAuthCallback from "./pages/OAuthCallback.jsx";
import Landing from "./pages/Landing.jsx";
import Dashboard from "./pages/Dashboard.jsx";
import EntityPage from "./pages/EntityPage.jsx";
import Sales from "./pages/Sales.jsx";
import Notifications from "./pages/Notifications.jsx";
import Showroom from "./pages/Showroom.jsx";

function Guard() {
  return currentUser() ? <Outlet /> : <Navigate to="/login" replace />;
}

function Shell() {
  const user = currentUser();
  const nav = useNavigate();
  const role = user?.role;
  const staff = ["ADMIN", "MANAGER", "SALES_AGENT"].includes(role);
  const admin = role === "ADMIN";
  const desk = ["ADMIN", "MANAGER"].includes(role);
  const customer = role === "CUSTOMER";

  return (
    <div className="app">
      <aside className="side">
        <Link to="/" className="brand brand-link">
          <Logo />
          <div>
            <h1>CDMS</h1>
            <p>Kigali showroom</p>
          </div>
        </Link>
        <nav>
          {customer && <NavLink to="/app/showroom">Showroom</NavLink>}
          {staff && <NavLink to="/app" end>Dashboard</NavLink>}
          {staff && <NavLink to="/app/cars">Inventory</NavLink>}
          {desk && <NavLink to="/app/customers">Customers</NavLink>}
          {staff && <NavLink to="/app/sales">{role === "SALES_AGENT" ? "Requests" : "Sales"}</NavLink>}
          {admin && <NavLink to="/app/employees">Staff</NavLink>}
          {desk && <NavLink to="/app/notifications">Notifications</NavLink>}
        </nav>
        <p className="note">{user?.fullName}<br /><span className={`badge role-pill ${role}`}>{role}</span></p>
        <button className="btn btn-signout" type="button" onClick={() => { clearSession(); nav("/"); }}>
          Sign out
        </button>
      </aside>
      <main className="main"><Outlet /></main>
    </div>
  );
}

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<Landing />} />
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />
      <Route path="/oauth/callback" element={<OAuthCallback />} />
      <Route element={<Guard />}>
        <Route path="/app" element={<Shell />}>
          <Route index element={<HomeRedirect />} />
          <Route path="showroom" element={<Showroom />} />
          <Route path="cars" element={<EntityPage entity="cars" />} />
          <Route path="customers" element={<EntityPage entity="customers" />} />
          <Route path="employees" element={<EntityPage entity="employees" />} />
          <Route path="sales" element={<Sales />} />
          <Route path="notifications" element={<Notifications />} />
        </Route>
      </Route>
    </Routes>
  );
}

function HomeRedirect() {
  const user = currentUser();
  if (user?.role === "CUSTOMER") {
    return <Navigate to="/app/showroom" replace />;
  }
  return <Dashboard />;
}
