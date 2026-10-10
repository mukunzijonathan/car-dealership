# What to put in the report (and say in the defense)

MUKUNZI Jonathan — 27132. Copy from `docs/requirements.md` and `docs/domain-model.md` instead of rewriting them.

**GitHub (one repo only):** https://github.com/mukunzijonathan/car-dealership  
Do not create another repository. Open the pull request into `main` of this repo.

## 1. Problem, users, objectives, quality, stories

Paste `docs/requirements.md`.

Add two screenshots: home page on a wide laptop, and the same page at ~400px (DevTools phone mode). That is the mock-up / responsive evidence for item 3.

## 2. Domain and data modeling

| Part | File | How to show it |
| --- | --- | --- |
| 2a Class / ER | `docs/uml-class.puml`, `docs/use-case.puml` | IntelliJ PlantUML → Export PNG |
| 2b Concepts | `docs/domain-model.md` | Paste actors / processes / data objects |
| 2c Physical schema | `docs/er-schema.puml` | Export PNG + pgAdmin tables |

Say: operational data is PostgreSQL (PK/FK/unique VIN). Notifications are MongoDB documents with no foreign keys.

## 3. Front end

React 19 + Vite. Pages: public landing, login, register, customer showroom, staff dashboard / inventory / sales / notifications.

Screenshot: landing, register, login, customer showroom, agent accepting a request.

## 4. Architecture

Layered monolith:

`React (5173)` → `Spring Boot controllers` → `services` → `JPA / Mongo repositories`  
Register or approve sale → `RabbitMQ` → listeners → `MongoDB notifications`

Package-by-feature under `rw.ac.auca`.

## 5. Both databases

| Store | Database | Holds |
| --- | --- | --- |
| PostgreSQL (host) | `carDealership` | `car`, `customer`, `employee`, `sale`, `app_users` |
| MongoDB (Docker) | `cdms_docs` | `notifications`, `audit_logs` |

How to open them: README section “How to show PostgreSQL, MongoDB and RabbitMQ”.

## 6. Authentication — OAuth2, not OTP

The instruction says *“secure authentication methods, including OAuth2 mechanisms.”* That is **not** OTP.

Write this:

> Customers create an account on `/register` (name, email, phone, password). Passwords are stored with BCrypt. Login returns a JWT. The React app sends `Authorization: Bearer …`. Staff accounts are created only by an admin.  
>  
> OAuth2 is implemented with Google (`/oauth2/authorization/google`). The first successful Google login creates an `app_users` row and a `customer` row, then issues the same JWT.  
>  
> CDMS does not send a one-time password. After register, RabbitMQ queue `cdms.account.registered` writes a welcome email and a welcome SMS **document** into MongoDB. Sale approval uses the same broker for confirmation messages. Those are notifications, not login codes.

Screenshot: the register page, then Mongo `notifications` with subject `Welcome to CDMS`.

If Google is not configured, say so honestly: the provider is wired; the demo login is email/password.

## 7. RabbitMQ

Queues:

- `cdms.account.registered` — welcome email/SMS after a customer registers
- `cdms.sale.created` — after a staff member accepts a sale
- `cdms.notification.email`
- `cdms.notification.sms`

Console: http://localhost:15672 (`guest` / `guest`).

Demo: register a new customer **or** accept a sale, then show the console and Admin → Notifications.

## 8. RBAC

| Role | Allowed |
| --- | --- |
| Visitor | Browse lot |
| CUSTOMER | Register, request, see own sales |
| SALES_AGENT | Inventory, accept/reject |
| MANAGER | Customers, all sales, Mongo notifications |
| ADMIN | Staff accounts as well |

Enforced with `@PreAuthorize` and Spring Security.

## 9. Git — same repo, pull request into main

Repository: https://github.com/mukunzijonathan/car-dealership  

1. Branch `feature/cdms-final`
2. Push that branch
3. Open a pull request **into `main`**
4. Screenshot the open PR

Do not create a second GitHub repo. Do not `git push origin main` until that PR exists.

## Live demo script (about 4 minutes)

1. `docker compose up -d` → `docker ps` (`cdms-mongo`, `cdms-rabbitmq`).
2. http://localhost:5173 — browse, filter by brand, no login.
3. **Create account** (new email) → showroom. Then Mongo/Notifications: welcome email + SMS.
4. Request a car under the credit limit (Golf, Corolla, Prius).
5. Sign out → `agent@cdms.rw` / `Agent#123` → Accept.
6. pgAdmin: `sale` APPROVED, `car` SOLD.
7. RabbitMQ console: queues moved.
8. `admin@cdms.rw` — Staff page (admin-only).

## Honest limitations

- Localhost only.
- Email/SMS are queued and stored in Mongo; no Gmail/Twilio send.
- Google OAuth needs a client ID you create in Google Cloud if you want the button live.
- Automated tests are thin; Postman is the QA evidence.
