# CDMS — Car Dealership Management System

MUKUNZI Jonathan — 27132  
Adventist University of Central Africa — INSY 8322 Web Technology

Web application for a Kigali car showroom. Visitors browse inventory. Customers register and request a car. Staff accept or reject the request. PostgreSQL stores operational data. RabbitMQ carries registration and sale events. MongoDB stores email, SMS and audit documents.

**Repository:** https://github.com/mukunzijonathan/car-dealership  
**Pull request into main:** https://github.com/mukunzijonathan/car-dealership/pull/1  
**Project documentation:** [docs/CDMS-Project-Documentation.md](docs/CDMS-Project-Documentation.md)

## Layout

```
backend/           Spring Boot API (port 8080)
frontend/          React + Vite (port 5173)
docker-compose.yml MongoDB and RabbitMQ
docs/              Requirements, models, project documentation
postman/           API collection
pics/              Vehicle photographs
```

## Requirements

- PostgreSQL with database `carDealership`
- Docker
- JDK 21 and Maven
- Node.js 18 or later

## Run

```bash
docker compose up -d
cd backend && mvn spring-boot:run
```

```bash
cd frontend && npm install && npm run dev
```

| Service | URL |
| --- | --- |
| User interface | http://localhost:5173 |
| API | http://localhost:8080 |
| RabbitMQ console | http://localhost:15672 (`guest` / `guest`) |
| MongoDB | `mongodb://localhost:27017` / `cdms_docs` |

## Demo accounts

| Role | Email | Password |
| --- | --- | --- |
| Administrator | admin@cdms.rw | Admin#123 |
| Manager | manager@cdms.rw | Manager#123 |
| Sales agent | agent@cdms.rw | Agent#123 |
| Customer | customer@cdms.rw | Customer#123 |

Google OAuth 2.0 uses `GOOGLE_CLIENT_ID` and `GOOGLE_CLIENT_SECRET` when those environment variables are set. Redirect URI: `http://localhost:8080/login/oauth2/code/google`.

## Documentation

| Document | Topic |
| --- | --- |
| [docs/CDMS-Project-Documentation.md](docs/CDMS-Project-Documentation.md) | Full project report |
| [docs/requirements.md](docs/requirements.md) | Problem, users, stories |
| [docs/domain-model.md](docs/domain-model.md) | Conceptual and physical model |
| [docs/uml-class.puml](docs/uml-class.puml) | Class diagram |
| [docs/er-schema.puml](docs/er-schema.puml) | Schema |
| [docs/use-case.puml](docs/use-case.puml) | Use cases |
| [postman/DUMMY-DATA.md](postman/DUMMY-DATA.md) | API samples |
