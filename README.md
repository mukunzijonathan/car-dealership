# CDMS — Car Dealership Management System

Web Technology final project. **MUKUNZI Jonathan — 27132**

A Kigali showroom system: visitors browse the lot, customers request a car, staff accept or reject, PostgreSQL keeps the business data, RabbitMQ carries the sale event, MongoDB stores the email/SMS/audit documents.

GitHub: **https://github.com/mukunzijonathan/car-dealership** — keep this repo. Do not create a second one.

```
backend/              Spring Boot API (port 8080)
frontend/             React + Vite (port 5173)
docker-compose.yml    MongoDB + RabbitMQ only
docs/                 Requirements, UML, ER, report notes
postman/              Collection + dummy JSON
pics/                 Car photos
```

PostgreSQL stays on the host (pgAdmin, database `carDealership`). MongoDB and RabbitMQ run in Docker.

---

## Brief vs what is left

| # | Instruction | Status | What you still do |
| --- | --- | --- | --- |
| 1 | Problem, users, objectives, quality, 2–3 stories | **Done** — `docs/requirements.md` | Copy into the report |
| 2a | ER / UML class diagram | **Done** — `docs/uml-class.puml` | Export a PNG for the report |
| 2b | Conceptual domain model | **Done** — `docs/domain-model.md` | Copy into the report |
| 2c | Physical schema (PK/FK/indexes) | **Done** — `docs/er-schema.puml` | Export a PNG; show tables in pgAdmin |
| 3 | Responsive React UI + mockups | **Done** — `frontend/` | Screenshot laptop + phone widths |
| 4 | Backend architecture | **Done** — layered Spring Boot | Explain controllers → services → repos |
| 5 | Relational + non-relational | **Done** — Postgres + Mongo | Show both in the demo (below) |
| 6 | Auth including OAuth2 | **Done for the demo** — customers self-register, JWT login, OAuth2 Google is wired. Not OTP. Welcome email/SMS go through RabbitMQ | Optional: add a Google client ID so the button works |
| 7 | RabbitMQ for email/SMS/events | **Done** | Approve a sale, then show queues + Mongo |
| 8 | RBAC | **Done** — ADMIN, MANAGER, SALES_AGENT, CUSTOMER | Log in as each role |
| 9 | Git + **pull request into main** | **Not done** | Do **not** push straight to `main`. Open a PR (see below) |

**Bonus (optional, extra marks):**

| Bonus | Status |
| --- | --- |
| DevOps / deployment | Partial — Docker Compose for Mongo + RabbitMQ. No cloud deploy/CI yet |
| Software testing | Thin — one disabled Spring Boot test. Postman collection is the practical QA |
| New technology | Already used: JWT, React, Docker, RabbitMQ, MongoDB |

Full report script: `docs/REPORT.md`.

---

## Start the system (after a reboot)

Open a terminal in this folder:

```bash
cd /home/spider/Innovation/WebTech/Assignments/27132_MUKUNZI_Jonathan_Assignment4
```

### 1. MongoDB + RabbitMQ (Docker)

```bash
docker compose up -d
docker ps
```

You must see `cdms-mongo` (27017) and `cdms-rabbitmq` (5672 and 15672).  
Do this **before** starting Spring Boot. If these containers are down, the API still starts but sale notifications will fail.

### 2. PostgreSQL

pgAdmin / host Postgres must already have database **`carDealership`**.  
User `postgres`. Hibernate `ddl-auto=update` creates/updates tables on startup.

### 3. Backend (IntelliJ or Maven)

IntelliJ: open the `backend` folder (the one with `pom.xml`) → run `CardealershipApplication`.

Or:

```bash
cd backend
mvn spring-boot:run
```

Wait until the log says `Started CardealershipApplication`. API: http://localhost:8080

### 4. Frontend

```bash
cd frontend
npm install
npm run dev
```

UI: **http://localhost:5173** (not 8080).

### Stop

Ctrl+C in the backend and frontend terminals. Containers can stay up, or:

```bash
docker compose down
```

(`down` does not delete the Mongo volume. `down -v` would wipe Mongo documents.)

---

## Demo logins

| Role | Email | Password | Opens |
| --- | --- | --- | --- |
| Admin | admin@cdms.rw | Admin#123 | Staff + inventory + sales + notifications |
| Manager | manager@cdms.rw | Manager#123 | Same without creating staff |
| Sales agent | agent@cdms.rw | Agent#123 | Inventory + pending requests |
| Customer | customer@cdms.rw | Customer#123 | Showroom + my requests |
| Dummy customer | patrick.habimana@gmail.com | Customer#123 | Same as customer |
| Dummy customer | diane.uwimana@gmail.com | Customer#123 | Same as customer |

Visitors browse http://localhost:5173 with **no account**. Requesting a car needs a customer login.

About **25** dummy cars are seeded on first start (they are not wiped later).

---

## How to show PostgreSQL, MongoDB and RabbitMQ

Use this order in the video or defense.

### PostgreSQL (operational data)

1. Open **pgAdmin** → database `carDealership` → Schemas → public → Tables.
2. Point at `car`, `customer`, `employee`, `sale`, `app_users`.
3. Right-click `car` → View/Edit Data. Show VIN, status, price.
4. Request a car in the UI, refresh `sale` and `car` — the car is `RESERVED`, a `sale` row is `REQUESTED`.

What to say:

> PostgreSQL holds the operational data: cars, people, users and sales. Those are relational rows with primary keys and foreign keys.

### RabbitMQ (events)

1. After `docker compose up -d`, open http://localhost:15672  
   Login `guest` / `guest`.
2. Queues tab: `cdms.sale.created`, `cdms.notification.email`, `cdms.notification.sms`.
3. Log in as **agent@cdms.rw**, open Requests, **Accept** a pending sale.
4. Refresh Queues — message counts move. Backend log prints `[EMAIL]` and `[SMS]`.

What to say:

> I do not send a real SMS. RabbitMQ is the broker. Approving a sale publishes an event. Listeners pretend to send email and SMS.

### MongoDB (documents)

After that approve:

**Option A — Compass** (if installed): connect `mongodb://localhost:27017` → database `cdms_docs` → collections `notifications` and `audit_logs`.

**Option B — terminal:**

```bash
docker exec -it cdms-mongo mongosh cdms_docs --eval 'db.notifications.find().pretty()'
docker exec -it cdms-mongo mongosh cdms_docs --eval 'db.audit_logs.find().pretty()'
```

**Option C — the app:** log in as admin or manager → **Notifications**. Those rows are Mongo documents, not Postgres.

What to say:

> MongoDB stores documents: the email, the SMS, and the audit log. No joins. `relatedSaleId` is just the sale UUID as a string.

### One sentence for the report

> I kept PostgreSQL on the host for cars, customers, staff and sales. MongoDB and RabbitMQ run in Docker Compose on localhost. Spring Boot talks to `localhost:5432`, `localhost:27017` and `localhost:5672`. When a sales agent approves a request, a RabbitMQ event is published and listeners write email and SMS documents into MongoDB.

---

## Use the existing GitHub repo

Use **https://github.com/mukunzijonathan/car-dealership**. That is already `origin`. A new empty repo would split the history and confuse the lecturer.

Put the final code on a feature branch and open a pull request **into `main` of that same repo**.

## Requirement 9 — do not push straight to `main`

You are already on local `main`. GitHub `origin/main` still has the old Assignment 3 API (`Add Spring Boot car dealership REST API.`).

The new CDMS work is **not committed**. If you `git add` + `commit` on `main` and `git push`, you update `main` directly. That is **not** a pull request. The brief asks for a PR **into** `main`.

Do this instead (when you are ready — I will not push unless you ask):

```bash
cd /home/spider/Innovation/WebTech/Assignments/27132_MUKUNZI_Jonathan_Assignment4

# 1. Leave main. Put the final work on a feature branch.
git checkout -b feature/cdms-final

# 2. Stage the project (not editor junk).
git add backend frontend docker-compose.yml docs pics postman README.md .gitignore

# 3. Commit
git commit -m "$(cat <<'EOF'
Complete CDMS final project: React showroom, JWT, Postgres, MongoDB and RabbitMQ.

EOF
)"

# 4. Push the branch, not main
git push -u origin feature/cdms-final
```

Then on GitHub: **Compare & pull request** → base **`main`** ← compare **`feature/cdms-final`** → Create pull request.

That PR is requirement 9. You can merge it after the lecturer sees it, or leave it open.

If you already committed on `main` by mistake: still create the branch from that commit, push the branch, and open the PR. Avoid `git push origin main` until the PR exists.

---

## Authentication (requirement 6) — not OTP

The brief says **OAuth2**, not OTP.

| What people mix up | What CDMS actually does |
| --- | --- |
| OTP / SMS code to log in | **Not used.** No one-time password. |
| Create an account | **Yes.** `/register` creates a Customer + AppUser. Welcome email/SMS go through RabbitMQ into Mongo. |
| Sign in | Email + password → JWT |
| OAuth2 | **Google** at `/oauth2/authorization/google`. First Google login also creates the customer account |

Demo the register form. Then show Mongo `notifications` with subject `Welcome to CDMS`. That is the “we sent email/SMS” evidence.

Google button appears only after you set a real client ID (otherwise the login page says OAuth2 is implemented but not configured):

```bash
export GOOGLE_CLIENT_ID='your-id'
export GOOGLE_CLIENT_SECRET='your-secret'
```

Redirect URI in Google Cloud: `http://localhost:8080/login/oauth2/code/google`

You can submit without Google credentials. Write that OAuth2 is implemented in Spring Security and JWT is the login you demo.

---

## Postman

Import `postman/CDMS.postman_collection.json`. Dummy bodies: `postman/DUMMY-DATA.md`.

Run **Auth → Login admin** first. The collection stores `{{token}}`.
