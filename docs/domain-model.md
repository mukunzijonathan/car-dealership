# 2. Domain and data modeling

## 2b. Conceptual model (no tables yet)

**Actors:** Visitor, Customer, Sales agent, Manager, Admin, Notification service (email/SMS).

**Processes:** browse stock → register/login → request sale → staff accept/reject → notify customer.

**Data objects:** Person, Car, Sale, User account, Role, Notification document, Audit event.

- A **Person** is either a **Customer** or an **Employee**.
- An **AppUser** is the login (email + password + role).
- A **Sale** links one Car, one Customer, and (after approval) one Employee.
- **Notification** and **AuditLog** are documents, not rows you join.

## 2a. UML class diagram

See `uml-class.puml`. Open it in IntelliJ PlantUML, or:

```bash
# if plantuml is installed
plantuml docs/uml-class.puml
```

Core associations (multiplicity both ends):

- Customer 1 — 0..* Sale
- Employee 0..1 — 0..* Sale (null until approved)
- Car 1 — 0..1 Sale
- AppUser 1 — 1 Role
- Employee / Customer share Person fields (generalization)

## 2c. Technology schema

### PostgreSQL (`carDealership`) — operational data

| Table | PK | Important columns / constraints |
| --- | --- | --- |
| `app_users` | `id` UUID | `email` UNIQUE, `role` (ADMIN, MANAGER, SALES_AGENT, CUSTOMER), `password_hash` |
| `car` | `id` UUID | `vin` UNIQUE length 17, `status`, `price > 0`, `image_file` |
| `customer` | `id` UUID | `email` UNIQUE, `credit_limit >= 0` |
| `employee` | `id` UUID | `employee_number` UNIQUE `EMP-9999`, `role`, `salary` |
| `sale` | `id` UUID | `car_id` UNIQUE FK, `customer_id` FK, `employee_id` FK nullable, `status`, `sold_on` nullable |

Indexes: unique on `car.vin`, `customer.email`, `employee.employee_number`, `app_users.email`, `sale.car_id`.

### MongoDB (`cdms_docs`) — documents / operation history

| Collection | Typical fields |
| --- | --- |
| `notifications` | channel, recipient, subject, message, relatedSaleId, status, createdAt |
| `audit_logs` | action, actorEmail, details, relatedSaleId, createdAt |

No foreign keys. `relatedSaleId` is the UUID stored as a string.

See `er-schema.puml` for the picture.
