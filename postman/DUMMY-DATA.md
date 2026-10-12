# Postman dummy data

Import `CDMS.postman_collection.json` into Postman (Import → File).

1. Run **Auth / Login admin** first.
2. The collection saves `token` automatically.
3. Other requests send `Authorization: Bearer {{token}}`.

Base URL: `http://localhost:8080`

## Logins

| Who | Email | Password |
| --- | --- | --- |
| Admin | admin@cdms.rw | Admin#123 |
| Manager | manager@cdms.rw | Manager#123 |
| Agent | agent@cdms.rw | Agent#123 |
| Customer | customer@cdms.rw | Customer#123 |
| Dummy customer | patrick.habimana@gmail.com | Customer#123 |
| Dummy customer | diane.uwimana@gmail.com | Customer#123 |

Restart the API once so the extra 18 cars and dummy customers are inserted. Public lot: `GET /car/search-lot` (no token).

## Register a new customer

`POST /auth/register` — no token

```json
{
  "firstName": "Patrick",
  "lastName": "Habimana",
  "email": "patrick.habimana@gmail.com",
  "phoneNumber": "+250788333444",
  "password": "Patrick#123"
}
```

## Create a car (admin / manager / agent)

VIN must be **17 characters** and unique.

```json
{
  "vin": "1N4AL3AP7DC123456",
  "brand": "Mazda",
  "model": "CX-5",
  "manufactureYear": 2023,
  "price": 32000000,
  "status": "AVAILABLE",
  "imageFile": "landcruiser.jpg"
}
```

More dummy VINs: `JH4KA8260MC000111`, `WVWZZZ3CZWE000222`, `SALVA2BG6CH000333`

## Create a customer record (staff)

```json
{
  "firstName": "Diane",
  "lastName": "Uwimana",
  "email": "diane.uwimana@gmail.com",
  "phoneNumber": "+250788555666",
  "creditLimit": 40000000
}
```

## Create staff + portal login (admin only)

```json
{
  "firstName": "Bosco",
  "lastName": "Mugisha",
  "email": "bosco.mugisha@cdms.rw",
  "phoneNumber": "+250788777888",
  "employeeNumber": "EMP-0004",
  "hireDate": "2024-02-01",
  "role": "SALES_AGENT",
  "salary": 480000,
  "portalPassword": "Bosco#1234"
}
```

Then login as `bosco.mugisha@cdms.rw` / `Bosco#1234`.

## Customer requests a car

Login as customer, copy a car `id` from `GET /car/search-available`.

```json
{
  "car": { "id": "PASTE-CAR-UUID-HERE" },
  "notes": "I can visit the showroom on Saturday"
}
```

`POST /sale/request`

## Agent accepts / rejects

Login as agent. Copy sale `id` from `GET /sale/pending`.

- Accept: `POST /sale/approve/{{saleId}}`
- Reject: `POST /sale/reject/{{saleId}}`

## Expected failures (good for the video)

| Body | Expected message |
| --- | --- |
| VIN `ABC` | VIN must be 17 characters |
| Duplicate VIN | A car with this VIN already exists |
| Manager salary `100000` | MANAGER and ADMIN salaries must be at least RWF 500,000 |
| Customer `GET /employee/search-all` | You are not allowed to do this |
