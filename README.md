# NexGen — Employee Management System


A full-stack Employee Management System built with **Spring Boot 3**, **MySQL**, and a
**Three.js HTML/CSS/JS** responsive front-end. Built as a learning project to practice
end-to-end Java web development: REST APIs, JPA/Hibernate, validation, layered
architecture, and a hand-rolled front-end talking to that API.

---

## Features

- **Employee CRUD** — create, view, update, delete employee records
- **Department CRUD** — organize employees into departments
- **Search & filter** — search by name/email/title, filter by department and status
- **Sorting & pagination** — click column headers to sort; paged results server-side
- **Validation** — server-side (Bean Validation) and client-side, with field-level error messages
- **Responsive UI** — collapsible sidebar nav, mobile-friendly table and forms
- **Clean layering** — Controller → Service → Repository, DTOs in/out, global exception handling

---

## Tech Stack

| Layer          | Technology                                |
|----------------|--------------------------------------------|
| Language       | Java 17                                    |
| Framework      | Spring Boot 3.3 (Web, Data JPA, Validation) |
| Database       | MySQL 8 |
| ORM            | Hibernate / Spring Data JPA                |
| Build tool     | Maven                                      |
| Front-end      | HTML5, CSS3 (no framework), vanilla JavaScript (fetch API) |
| Boilerplate    | Lombok                                     |

---

## Project Structure

```
employee-management-app/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/com/company/ems/
│   │   │   ├── EmployeeManagementApplication.java   # entry point
│   │   │   ├── controller/                          # REST controllers
│   │   │   │   ├── EmployeeController.java
│   │   │   │   └── DepartmentController.java
│   │   │   ├── service/                             # business logic
│   │   │   │   ├── EmployeeService.java
│   │   │   │   └── DepartmentService.java
│   │   │   ├── repository/                          # Spring Data JPA
│   │   │   │   ├── EmployeeRepository.java
│   │   │   │   ├── DepartmentRepository.java
│   │   │   │   └── EmployeeSpecifications.java       # dynamic search/filter
│   │   │   ├── model/                                # JPA entities
│   │   │   │   ├── Employee.java
│   │   │   │   └── Department.java
│   │   │   ├── dto/                                  # request/response contracts
│   │   │   ├── exception/                            # custom exceptions + @RestControllerAdvice
│   │   │   └── config/
│   │   │       └── DataSeeder.java                   # demo data on first run
│   │   └── resources/
│   │       ├── application.properties                # MySQL config
│   │       └── static/                                # front-end (served at /)
│   │           ├── index.html
│   │           ├── css/styles.css
│   │           └── js/{api.js, app.js}
│   └── test/
│       └── java/com/company/ems/EmployeeServiceTest.java
└── README.md
```

---

## Prerequisites

- JDK 17+
- Maven 3.8+
- MySQL 8+

---

## Setup & Run

### MySQL setup

1. **Create the database** (or let the app create it — see below):
   ```sql
   CREATE DATABASE employee_management;
   ```

2. **Configure credentials** in `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/employee_management?useSSL=false&serverTimezone=UTC&createDatabaseIfNotExist=true
   spring.datasource.username=root
   spring.datasource.password=${DB_PASSWORD:}
   ```
   Set `DB_PASSWORD` in your environment to the password for your local MySQL
   instance. The application reads it from the `spring.datasource.password`
   property.

3. **Run the app**:
   ```bash
   mvn spring-boot:run
   ```

4. Hibernate will auto-create the `employees` and `departments` tables
   (`spring.jpa.hibernate.ddl-auto=update`), and `DataSeeder` will insert a
   handful of demo employees/departments on first boot.

5. Open **http://localhost:8080** in your browser.

### Running tests

```bash
mvn test
```

Tests run against the configured MySQL database in `src/test/resources/application.properties`.

---

## REST API Reference

Base URL: `http://localhost:8080/api`

### Employees

| Method | Endpoint                 | Description                                   |
|--------|---------------------------|------------------------------------------------|
| GET    | `/employees`              | Search/list employees (paged) — see query params below |
| GET    | `/employees/{id}`         | Get one employee                               |
| POST   | `/employees`              | Create an employee                             |
| PUT    | `/employees/{id}`         | Update an employee                             |
| DELETE | `/employees/{id}`         | Delete an employee                             |

**Query params for `GET /employees`:**
`keyword`, `departmentId`, `status` (`ACTIVE`/`ON_LEAVE`/`TERMINATED`),
`page` (default 0), `size` (default 10), `sortBy` (default `id`), `direction` (`asc`/`desc`)

**Example create payload:**
```json
{
  "firstName": "Asha",
  "lastName": "Rao",
  "email": "asha.rao@company.com",
  "phone": "+91-9876543210",
  "jobTitle": "Senior Software Engineer",
  "salary": 1450000,
  "hireDate": "2021-06-14",
  "status": "ACTIVE",
  "departmentId": 1
}
```

### Departments

| Method | Endpoint                  | Description                    |
|--------|----------------------------|---------------------------------|
| GET    | `/departments`              | List all departments (with employee counts) |
| GET    | `/departments/{id}`         | Get one department              |
| POST   | `/departments`              | Create a department             |
| PUT    | `/departments/{id}`         | Update a department             |
| DELETE | `/departments/{id}`         | Delete a department (blocked if it still has employees) |

### Error format

All errors return a consistent shape:
```json
{
  "timestamp": "2026-07-06T10:15:30",
  "status": 400,
  "error": "Validation Failed",
  "message": "One or more fields are invalid",
  "path": "/api/employees",
  "fieldErrors": { "email": "Email must be valid" }
}
```

---

## Design Notes / Learning Highlights

- **Layered architecture**: controllers stay thin (HTTP concerns only), services own
  business rules and transactions, repositories are pure data access.
- **DTOs, not entities, over the wire**: `EmployeeRequest`/`EmployeeResponse` decouple the
  API contract from the JPA model, avoiding lazy-loading/serialization issues and letting
  each side evolve independently.
- **Dynamic queries via JPA Specifications** (`EmployeeSpecifications`) power the combined
  keyword + department + status filtering without hand-writing multiple repository methods.
- **Centralized error handling** (`GlobalExceptionHandler`) keeps every endpoint's error
  response shape consistent, including Bean Validation field errors.
- **No frontend framework** — the UI is deliberately vanilla JS/CSS so the whole stack is
  readable without a build step, which keeps the focus on the Java/Spring side while still
  exercising real fetch-based API integration, client-side validation feedback, and
  responsive layout.

---

## Possible Extensions

- Add Spring Security (JWT-based auth, role-based access for HR vs. managers)
- Replace `ddl-auto=update` with Flyway/Liquibase migrations
- Add file upload for employee photos/documents
- Add an audit trail (created/modified timestamps + user) via Spring Data auditing
- Add CSV/Excel export of the employee roster
