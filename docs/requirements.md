# 1. Requirements (CDMS)

MUKUNZI Jonathan — 27132  
Web Technology final project, 2026-2027

## Problem statement

A Kigali car showroom still tracks stock, buyers and sales in notebooks or spreadsheets. Duplicate VINs, a sold car being offered again, and no record of who approved a sale are common. CDMS is a local web system that keeps inventory, customers, staff and sale requests in one place, with roles and an audit trail.

## Target users

| Actor | Goal |
| --- | --- |
| Visitor | Browse available cars without an account |
| Customer | Create an account, request a car |
| Sales agent | Accept or reject a customer request |
| Manager | Watch stock, customers and sales |
| Admin | Create staff portal accounts and manage the lot |

## Objectives

- Record every car, customer, employee and sale request.
- Stop a sold or reserved car from being sold twice.
- Restrict screens and APIs by role (RBAC).
- After a sale is approved, notify the customer (email/SMS via RabbitMQ) and keep the message in MongoDB.

## Scope

**In:** inventory, customer self-register, staff created by admin, request/approve sale, JWT login, PostgreSQL + MongoDB + RabbitMQ, React UI on localhost.

**Out:** native mobile apps, real mobile-money payment, OTP codes, production hosting (unless the DevOps bonus is finished).

**Authentication (item 6):** customers create an account with email and password (or Google OAuth2). Login returns a JWT. The brief asks for OAuth2, not OTP. Welcome email/SMS after register go through RabbitMQ into MongoDB. They are stored documents, not a one-time login code.

## Functional / business rules

- VIN is unique and exactly 17 characters.
- A customer may request only an AVAILABLE car; it becomes RESERVED.
- Only staff can approve; then the car is SOLD and a RabbitMQ event is published.
- Rejected requests put the car back to AVAILABLE.
- Last ADMIN cannot be deleted.
- Manager/Admin salary ≥ RWF 500,000.
- Customers cannot create staff accounts.

## Quality attributes

| Attribute | Measure |
| --- | --- |
| Usability | Landing, login and showroom usable on a laptop browser without horizontal scroll |
| Performance | List endpoints return in under 2s on localhost with seed data |
| Reliability | Sale request and approve run in a database transaction |
| Security | JWT on protected routes; RBAC; passwords stored with BCrypt |

## User stories

**US-1 — Browse**  
As a visitor I can see available cars on the home page so I know what is on the lot.  
**Acceptance:** `GET /car/search-available` works without a token; photos and price are shown.

**US-2 — Request**  
As a registered customer I request a car so a sales agent can complete the sale.  
**Acceptance:** car status becomes RESERVED; a `sale` row exists with status REQUESTED; I see it under My requests.

**US-3 — Approve**  
As a sales agent I accept a request so the car is marked sold and the customer is notified.  
**Acceptance:** status APPROVED / SOLD; MongoDB notification documents appear; RabbitMQ queues `cdms.notification.email` and `cdms.notification.sms` receive the event.
