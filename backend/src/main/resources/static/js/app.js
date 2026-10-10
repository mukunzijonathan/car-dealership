const API = "";
const routes = { cars: "car", customers: "customer", employees: "employee" };

const fields = {
  cars: [
    ["vin", "VIN (17 chars)", "text"],
    ["brand", "Brand", "text"],
    ["model", "Model", "text"],
    ["manufactureYear", "Year", "number"],
    ["price", "Price (RWF)", "number"],
    ["status", "Status", "select", ["AVAILABLE", "RESERVED", "SOLD"]],
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
  ],
};

const titles = {
  dashboard: ["Dashboard", "A single glance at the dealership floor."],
  cars: ["Inventory", "Every vehicle on the lot, with VIN and status."],
  customers: ["Customers", "People who buy — unique email, credit limit enforced."],
  employees: ["Staff", "Agents, managers and the one required admin."],
};

let state = { entity: "cars", editingId: null, cache: {} };

const $ = (id) => document.getElementById(id);

function toast(msg) {
  const el = $("toast");
  el.textContent = msg;
  el.style.display = "block";
  setTimeout(() => { el.style.display = "none"; }, 2400);
}

async function api(path, options = {}) {
  const res = await fetch(API + path, {
    headers: { "Content-Type": "application/json", ...(options.headers || {}) },
    ...options,
  });
  if (res.status === 204) return null;
  const data = await res.json().catch(() => ({}));
  if (!res.ok) {
    const fieldMsg = data.fields ? Object.values(data.fields).join(" · ") : "";
    throw new Error(fieldMsg || data.message || "Request failed");
  }
  return data;
}

function money(n) {
  return Number(n).toLocaleString("en-RW");
}

function badge(v) {
  return `<span class="badge ${v}">${v.replace("_", " ")}</span>`;
}

async function loadAll() {
  const [cars, customers, employees] = await Promise.all([
    api("/car/search-all"),
    api("/customer/search-all"),
    api("/employee/search-all"),
  ]);
  state.cache = { cars, customers, employees };
  return state.cache;
}

function renderDashboard() {
  const { cars = [], customers = [], employees = [] } = state.cache;
  const available = cars.filter((c) => c.status === "AVAILABLE").length;
  $("view-dashboard").innerHTML = `
    <div class="stats">
      <div class="stat"><span>Cars on lot</span><strong>${cars.length}</strong></div>
      <div class="stat"><span>Available</span><strong>${available}</strong></div>
      <div class="stat"><span>Customers</span><strong>${customers.length}</strong></div>
      <div class="stat"><span>Staff</span><strong>${employees.length}</strong></div>
    </div>
    <div class="panel">
      <table>
        <thead><tr><th>Brand</th><th>Model</th><th>Year</th><th>Price</th><th>Status</th></tr></thead>
        <tbody>
          ${cars.slice(0, 6).map((c) => `<tr>
            <td>${c.brand}</td><td>${c.model}</td><td>${c.manufactureYear}</td>
            <td>RWF ${money(c.price)}</td><td>${badge(c.status)}</td>
          </tr>`).join("") || `<tr><td colspan="5" class="empty">No cars yet</td></tr>`}
        </tbody>
      </table>
    </div>`;
}

function columns(entity) {
  if (entity === "cars") {
    return {
      head: "<tr><th>VIN</th><th>Brand</th><th>Model</th><th>Year</th><th>Price</th><th>Status</th><th></th></tr>",
      row: (c) => `<tr>
        <td>${c.vin}</td><td>${c.brand}</td><td>${c.model}</td><td>${c.manufactureYear}</td>
        <td>RWF ${money(c.price)}</td><td>${badge(c.status)}</td>
        <td class="actions">
          <button class="btn btn-ghost small" data-edit="${c.id}">Edit</button>
          <button class="btn btn-danger small" data-del="${c.id}">Delete</button>
        </td></tr>`,
    };
  }
  if (entity === "customers") {
    return {
      head: "<tr><th>Name</th><th>Email</th><th>Phone</th><th>Credit</th><th>Registered</th><th></th></tr>",
      row: (c) => `<tr>
        <td>${c.firstName} ${c.lastName}</td><td>${c.email}</td><td>${c.phoneNumber}</td>
        <td>RWF ${money(c.creditLimit)}</td><td>${c.registeredOn || ""}</td>
        <td class="actions">
          <button class="btn btn-ghost small" data-edit="${c.id}">Edit</button>
          <button class="btn btn-danger small" data-del="${c.id}">Delete</button>
        </td></tr>`,
    };
  }
  return {
    head: "<tr><th>No.</th><th>Name</th><th>Email</th><th>Role</th><th>Salary</th><th></th></tr>",
    row: (e) => `<tr>
      <td>${e.employeeNumber}</td><td>${e.firstName} ${e.lastName}</td><td>${e.email}</td>
      <td>${badge(e.role)}</td><td>RWF ${money(e.salary)}</td>
      <td class="actions">
        <button class="btn btn-ghost small" data-edit="${e.id}">Edit</button>
        <button class="btn btn-danger small" data-del="${e.id}">Delete</button>
      </td></tr>`,
  };
}

function renderList(entity) {
  const rows = state.cache[entity] || [];
  const cols = columns(entity);
  $("view-list").innerHTML = `
    <div class="panel">
      <table>
        <thead>${cols.head}</thead>
        <tbody>${rows.map(cols.row).join("") || `<tr><td colspan="7" class="empty">Nothing here yet.</td></tr>`}</tbody>
      </table>
    </div>`;
  $("view-list").querySelectorAll("[data-edit]").forEach((b) => {
    b.onclick = () => showForm(entity, b.dataset.edit);
  });
  $("view-list").querySelectorAll("[data-del]").forEach((b) => {
    b.onclick = () => remove(entity, b.dataset.del);
  });
}

function showForm(entity, id) {
  state.entity = entity;
  state.editingId = id;
  const record = id ? (state.cache[entity] || []).find((r) => r.id === id) : {};
  $("page-title").textContent = id ? "Edit record" : "New record";
  $("page-lede").textContent = "Business rules are applied in the service layer, same as in class.";
  $("new-btn").classList.add("hidden");
  $("view-dashboard").classList.add("hidden");
  $("view-list").classList.add("hidden");
  $("view-form").classList.remove("hidden");
  $("form-error").classList.remove("show");
  $("entity-form").innerHTML = fields[entity].map(([name, label, type, opts]) => {
    const val = record?.[name] ?? "";
    if (type === "select") {
      return `<label>${label}<select name="${name}">${opts.map((o) =>
        `<option ${o === val ? "selected" : ""}>${o}</option>`).join("")}</select></label>`;
    }
    return `<label>${label}<input name="${name}" type="${type}" value="${val}" required></label>`;
  }).join("") + `
    <div class="form-actions">
      <button type="button" class="btn btn-ghost" id="cancel-btn">Cancel</button>
      <button type="submit" class="btn btn-primary">Save</button>
    </div>`;
  $("cancel-btn").onclick = () => location.hash = `#/${entity}`;
}

async function save(event) {
  event.preventDefault();
  const form = new FormData($("entity-form"));
  const body = Object.fromEntries(form.entries());
  ["manufactureYear", "price", "creditLimit", "salary"].forEach((k) => {
    if (body[k] !== undefined) body[k] = Number(body[k]);
  });
  try {
    const resource = routes[state.entity];
    if (state.editingId) body.id = state.editingId;
    await api(state.editingId ? `/${resource}/update` : `/${resource}/create`, {
      method: state.editingId ? "PUT" : "POST",
      body: JSON.stringify(body),
    });
    toast("Saved");
    location.hash = `#/${state.entity}`;
  } catch (err) {
    const banner = $("form-error");
    banner.textContent = err.message;
    banner.classList.add("show");
  }
}

async function remove(entity, id) {
  if (!confirm("Delete this record?")) return;
  try {
    await api(`/${routes[entity]}/delete/${id}`, { method: "DELETE" });
    toast("Deleted");
    await loadAll();
    renderList(entity);
  } catch (err) {
    alert(err.message);
  }
}

async function route() {
  const name = (location.hash.replace("#/", "") || "dashboard").split("/")[0];
  document.querySelectorAll("nav a").forEach((a) => {
    a.classList.toggle("active", a.dataset.route === name);
  });
  await loadAll();
  $("view-form").classList.add("hidden");
  if (name === "dashboard") {
    $("page-title").textContent = titles.dashboard[0];
    $("page-lede").textContent = titles.dashboard[1];
    $("new-btn").classList.add("hidden");
    $("view-list").classList.add("hidden");
    $("view-dashboard").classList.remove("hidden");
    renderDashboard();
    return;
  }
  if (!fields[name]) return;
  $("page-title").textContent = titles[name][0];
  $("page-lede").textContent = titles[name][1];
  $("new-btn").classList.remove("hidden");
  $("new-btn").textContent = "New " + name.slice(0, -1);
  $("new-btn").onclick = () => showForm(name, null);
  $("view-dashboard").classList.add("hidden");
  $("view-list").classList.remove("hidden");
  state.entity = name;
  renderList(name);
}

$("entity-form").addEventListener("submit", save);
window.addEventListener("hashchange", route);
route().catch((err) => {
  $("view-dashboard").innerHTML = `<p class="empty">${err.message}. Start PostgreSQL, create database <code>car_dealership</code>, then run <code>mvn spring-boot:run</code>.</p>`;
});
