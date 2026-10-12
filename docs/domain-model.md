# Domain and data modeling — CDMS

## Conceptual model

**Actors:** visitor, customer, sales agent, manager, administrator, notification service.

**Processes:** browse stock → register or sign in → request a sale → staff accept or reject → notify the customer.

**Data objects:** Person, Car, Sale, user account, role, notification document, audit event.

- A person is either a customer or an employee.
- An application user is the login identity (email, credential, role, provider).
- A sale links one car, one customer, and after approval one employee.
- Notification and audit records are documents, not relational join tables.

## Class diagram

See `uml-class.puml`.

- Customer 1 — 0..* Sale  
- Employee 0..1 — 0..* Sale (absent until approval)  
- Car 1 — 0..1 Sale  
- Customer and Employee inherit Person attributes  

## Physical schema

### PostgreSQL (`carDealership`)

| Table | Primary key | Constraints |
| --- | --- | --- |
| `app_users` | `id` UUID | `email` UNIQUE; role enumeration |
| `car` | `id` UUID | `vin` UNIQUE, length 17; `price > 0` |
| `customer` | `id` UUID | `email` UNIQUE |
| `employee` | `id` UUID | `employee_number` UNIQUE; `email` UNIQUE |
| `sale` | `id` UUID | `car_id` UNIQUE FK; `customer_id` FK; `employee_id` FK nullable |

Indexes: unique on `car.vin`, `customer.email`, `employee.employee_number`, `app_users.email`, `sale.car_id`.

### MongoDB (`cdms_docs`)

| Collection | Fields |
| --- | --- |
| `notifications` | channel, recipient, subject, message, relatedSaleId, status, createdAt |
| `audit_logs` | action, actorEmail, details, relatedSaleId, createdAt |

See `er-schema.puml`.
