# Requirements — CDMS

MUKUNZI Jonathan — 27132  
INSY 8322 Web Technology

## Problem statement

A Kigali car showroom still tracks stock, buyers and sales in notebooks or spreadsheets. Duplicate VINs, a sold car being offered again, and no record of who approved a sale are common. CDMS is a local web system that keeps inventory, customers, staff and sale requests in one place, with roles and an audit trail.

## Target users

| Actor | Goal |
| --- | --- |
| Visitor | Browse available cars without an account |
| Customer | Create an account and request a car |
| Sales agent | Accept or reject a customer request |
| Manager | Supervise stock, customers and sales |
| Administrator | Create staff portal accounts and manage the lot |

## Objectives

- Record every car, customer, employee and sale request.
- Prevent a reserved or sold car from being sold twice.
- Restrict screens and APIs by role (RBAC).
- After a sale is approved, notify the customer through RabbitMQ and persist the message in MongoDB.

## Scope

**In scope:** inventory, customer self-registration, staff created by an administrator, sale request and approval, JWT and OAuth 2.0, PostgreSQL, MongoDB, RabbitMQ, React user interface.

**Out of scope:** native mobile applications, live payment, and public-cloud hosting.

## Functional / business rules

- VIN is unique and exactly 17 characters.
- A customer may request only an available car; it becomes reserved.
- Only staff can approve; the car is then sold and a RabbitMQ event is published.
- A rejected request returns the car to available.
- The last administrator cannot be deleted.
- Manager and administrator salary is at least RWF 500,000.
- Customers cannot create staff accounts.

## Quality attributes

| Attribute | Measure |
| --- | --- |
| Usability | Landing, login and showroom usable on a laptop without horizontal scroll |
| Performance | List endpoints return in under two seconds on localhost with seed data |
| Reliability | Sale request and approval run in a database transaction |
| Security | JWT on protected routes; RBAC; BCrypt passwords; OAuth 2.0 with Google |

## User stories

**US-1 — Browse**  
As a visitor I can see cars on the home page so I know what is on the lot.  
**Acceptance:** public car-list endpoints work without a token; photographs and prices are shown.

**US-2 — Request**  
As a registered customer I request a car so a sales agent can complete the sale.  
**Acceptance:** car status becomes RESERVED; a sale row exists with status REQUESTED; I see it under My requests.

**US-3 — Approve**  
As a sales agent I accept a request so the car is marked sold and the customer is notified.  
**Acceptance:** status APPROVED / SOLD; MongoDB notification documents appear; RabbitMQ email and SMS queues receive the event.
