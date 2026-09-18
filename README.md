# Car Dealership (Assignment 4)

Spring Boot project in the same style as the in-class **stockmanagementsystem**: package-by-feature, `BaseEntity`, service interface + `*ServiceImpl`, Lombok, PostgreSQL, REST mapped as `/entity/create` and `/entity/search-all`.

MUKUNZI Jonathan — 27132

- **GitHub:** `https://github.com/mukunzijonathan/car-dealership`

## Run

Create the database, then start the app (same way as class):

```sql
CREATE DATABASE "carDealership";
```

```bash
mvn spring-boot:run
```

Check `src/main/resources/application.properties` for the PostgreSQL user and password.

- UI: http://localhost:8080/
- Postman: `postman/CDMS-Assignment4.postman_collection.json`

## REST (class mapping)

| | Car | Customer | Employee |
| --- | --- | --- | --- |
| List | `GET /car/search-all` | `GET /customer/search-all` | `GET /employee/search-all` |
| One | `GET /car/search-by-id/{id}` | `GET /customer/search-by-id/{id}` | `GET /employee/search-by-id/{id}` |
| Create | `POST /car/create` | `POST /customer/create` | `POST /employee/create` |
| Update | `PUT /car/update` | `PUT /customer/update` | `PUT /employee/update` |
| Delete | `DELETE /car/delete/{id}` | `DELETE /customer/delete/{id}` | `DELETE /employee/delete/{id}` |

Business rules live in the service implementation (unique VIN/email, cannot delete a sold car, last ADMIN cannot be removed, manager salary ≥ RWF 500,000).

## Packages

```
rw.ac.auca.base              BaseEntity, Person
rw.ac.auca.car.domain|repository|service
rw.ac.auca.customer.domain|repository|service
rw.ac.auca.employee.domain|repository|service
rw.ac.auca.controller        CarController, CustomerController, EmployeeController
rw.ac.auca.cardealership     CardealershipApplication
```
