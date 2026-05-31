# RadioIsotop API

A RESTful backend for managing radioisotope medical treatments. Built with Spring Boot, it handles patient monitoring, treatment tracking, real-time telemetry from smart watches, and alert management — all following the FHIR healthcare interoperability standard.

---

## Table of Contents

- [Tech Stack](#tech-stack)
- [Architecture Overview](#architecture-overview)
- [Getting Started](#getting-started)
- [Authentication](#authentication)
- [Roles & Permissions](#roles--permissions)
- [API Endpoints](#api-endpoints)
  - [Auth](#auth)
  - [Users](#users)
  - [Patients](#patients)
  - [Doctors](#doctors)
  - [Departments](#departments)
  - [Family Contacts](#family-contacts)
  - [Treatments](#treatments)
  - [Telemetry](#telemetry)
  - [Alerts](#alerts)
  - [Devices (Watches)](#devices-watches)
- [Data Models](#data-models)
- [Project Structure](#project-structure)

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.0.3 |
| Security | Spring Security + JWT (JJWT 0.11.5) |
| ORM | Spring Data JPA (Hibernate) |
| Database | MySQL 8 |
| Healthcare Standard | HAPI FHIR R4 v6.10.0 |
| API Docs | SpringDoc OpenAPI 3 (Swagger UI) |
| Build | Maven |
| Infrastructure | Docker Compose (MySQL) |

---

## Architecture Overview

```
┌─────────────────────────────────────────────────────────┐
│                     REST Controllers                     │
│         (Auth, Paciente, Doctor, Tratamiento, ...)       │
└───────────────────────┬─────────────────────────────────┘
                        │
┌───────────────────────▼─────────────────────────────────┐
│                      Service Layer                       │
│     (Business logic + FHIR entity conversion)           │
└───────────────────────┬─────────────────────────────────┘
                        │
┌───────────────────────▼─────────────────────────────────┐
│                  Repository Layer                        │
│              (Spring Data JPA — MySQL)                   │
└─────────────────────────────────────────────────────────┘

Cross-cutting concerns:
  - JWT filter (validates Bearer token on every request)
  - GlobalExceptionHandler (uniform error responses)
  - Soft-delete pattern (activo flag on all entities)
  - CORS (localhost:5173 allowed)
```

All API responses are serialized as **FHIR R4 JSON** resources. The database uses a **soft-delete** strategy: records are never physically removed — instead, an `activo` flag is set to `false`.

---

## Getting Started

### Prerequisites

- Java 21+
- Maven 3.9+
- Docker & Docker Compose (for the database)

### 1. Start the database

```bash
cd radioisotopo
docker-compose up -d
```

This starts a MySQL 8 container on **port 3305** (mapped to 3306 inside the container), creates the `db_radioIsotopo` database, and runs `schema.sql` automatically.

### 2. Run the application

```bash
mvn spring-boot:run
```

The API starts on **[http://141.148.65.35:8081](http://141.148.65.35:8081)**.

### 3. Open Swagger UI

```
http://141.148.65.35:8081/swagger-ui/index.html
```

Use the **Authorize** button in Swagger to paste your JWT token and test authenticated endpoints.

---

## Authentication

The API uses **stateless JWT Bearer authentication**. Tokens are valid for **24 hours**.

### Login

```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "yourpassword"
}
```

### Register

```http
POST /api/auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "yourpassword",
  "nombre":"user",
  "role": "ADMIN"
}
```

Both endpoints return a JWT token in the response. Include it in all subsequent requests:

```
Authorization: Bearer <token>
```

---

## Roles & Permissions

The system has four roles with a hierarchical permission model:

```
ADMIN
  └── DOCTOR
        ├── PACIENTE
        └── FAMILIAR
```

| Role | Description |
|---|---|
| `ADMIN` | Full access to all resources. Can create/delete users, doctors, patients, departments, and devices. |
| `DOCTOR` | Can read and manage patients, treatments, telemetry, and alerts within their scope. |
| `PACIENTE` | Read-only access to their own data (profile, treatment, telemetry, alerts, device). |
| `FAMILIAR` | Read-only access to the patients they are linked to. |

All endpoints are protected with `@PreAuthorize` annotations. Attempting to access a resource outside your role returns `403 Forbidden`.

---

## API Endpoints

All endpoints are prefixed with `/api`.

### Auth

| Method | Path | Roles | Description |
|---|---|---|---|
| `POST` | `/auth/register` | Public | Register a new user |
| `POST` | `/auth/login` | Public | Login and receive a JWT token |

---

### Users

| Method | Path | Roles | Description |
|---|---|---|---|
| `GET` | `/usuarios` | ADMIN | List all active users |
| `GET` | `/usuarios/{id}` | ADMIN | Get user by ID |
| `GET` | `/usuarios/todos` | ADMIN | List all users including deleted |
| `GET` | `/usuarios/eliminados` | ADMIN | List only soft-deleted users |
| `DELETE` | `/usuarios/{id}` | ADMIN | Soft-delete a user |
| `GET` | `/usuarios/{id}/foto-perfil` | ADMIN | Get profile photo path |
| `GET` | `/usuarios/{id}/foto-perfil/imagen` | ADMIN | Download profile photo file |
| `PATCH` | `/usuarios/{id}/foto-perfil` | ADMIN | Upload/update profile photo |

---

### Patients

| Method | Path | Roles | Description |
|---|---|---|---|
| `POST` | `/pacientes` | ADMIN, DOCTOR | Create a new patient |
| `GET` | `/pacientes` | ADMIN, DOCTOR | List all active patients |
| `GET` | `/pacientes/{id}` | ADMIN, DOCTOR | Get patient by ID |
| `GET` | `/pacientes/me` | PACIENTE | Get own patient profile |
| `GET` | `/pacientes/mis-pacientes` | FAMILIAR | List patients linked to the caller |
| `GET` | `/pacientes/mis-pacientes/{id}` | FAMILIAR | Get a specific linked patient |
| `PUT` | `/pacientes/{id}` | ADMIN, DOCTOR | Update patient data |
| `DELETE` | `/pacientes/{id}` | ADMIN | Soft-delete a patient |
| `GET` | `/pacientes/todos` | ADMIN | List all patients including deleted |
| `GET` | `/pacientes/eliminados` | ADMIN | List only soft-deleted patients |

---

### Doctors

| Method | Path | Roles | Description |
|---|---|---|---|
| `POST` | `/doctores` | ADMIN | Create a new doctor |
| `GET` | `/doctores` | ADMIN | List all active doctors |
| `GET` | `/doctores/{id}` | ADMIN | Get doctor by ID |
| `GET` | `/doctores/me` | DOCTOR | Get own doctor profile |
| `PUT` | `/doctores/{id}` | ADMIN | Update doctor data |
| `DELETE` | `/doctores/{id}` | ADMIN | Soft-delete a doctor |
| `GET` | `/doctores/todos` | ADMIN | List all doctors including deleted |
| `GET` | `/doctores/eliminados` | ADMIN | List only soft-deleted doctors |

---

### Departments

| Method | Path | Roles | Description |
|---|---|---|---|
| `POST` | `/departamentos` | ADMIN | Create a department |
| `GET` | `/departamentos` | All roles | List all active departments |
| `GET` | `/departamentos/{id}` | All roles | Get department by ID |
| `PUT` | `/departamentos/{id}` | ADMIN | Update a department |
| `DELETE` | `/departamentos/{id}` | ADMIN | Soft-delete a department |
| `GET` | `/departamentos/todos` | ADMIN | List all including deleted |
| `GET` | `/departamentos/eliminados` | ADMIN | List only soft-deleted |

---

### Family Contacts

| Method | Path | Roles | Description |
|---|---|---|---|
| `POST` | `/familiares` | ADMIN, DOCTOR | Create a family contact |
| `GET` | `/familiares` | ADMIN, DOCTOR | List all active family contacts |
| `GET` | `/familiares/{id}` | ADMIN, DOCTOR | Get family contact by ID |
| `PUT` | `/familiares/{id}` | ADMIN, DOCTOR | Update a family contact |
| `DELETE` | `/familiares/{id}` | ADMIN | Soft-delete a family contact |
| `GET` | `/familiares/todos` | ADMIN | List all including deleted |
| `GET` | `/familiares/eliminados` | ADMIN | List only soft-deleted |

---

### Treatments

| Method | Path | Roles | Description |
|---|---|---|---|
| `POST` | `/tratamientos` | ADMIN, DOCTOR | Create a treatment |
| `GET` | `/tratamientos` | ADMIN, DOCTOR | List all treatments |
| `GET` | `/tratamientos/{id}` | ADMIN, DOCTOR | Get treatment by ID |
| `PUT` | `/tratamientos/{id}` | ADMIN, DOCTOR | Update a treatment |
| `DELETE` | `/tratamientos/{id}` | ADMIN | Soft-delete a treatment |
| `GET` | `/tratamientos/activos` | ADMIN, DOCTOR | List only active treatments |
| `GET` | `/tratamientos/paciente/{pacienteId}` | ADMIN, DOCTOR | Treatments for a specific patient |
| `GET` | `/tratamientos/doctor/{doctorId}` | ADMIN, DOCTOR | Treatments assigned to a doctor |
| `GET` | `/tratamientos/me` | PACIENTE | Get own treatment |
| `GET` | `/tratamientos/mis-pacientes/{pacienteId}` | FAMILIAR | Get treatment for a linked patient |
| `GET` | `/tratamientos/todos` | ADMIN | List all including deleted |
| `GET` | `/tratamientos/eliminados` | ADMIN | List only soft-deleted |

---

### Telemetry

Sensor readings (heart rate, steps, temperature, radiation) pushed from smart watches.

| Method | Path | Roles | Description |
|---|---|---|---|
| `POST` | `/telemetria` | ADMIN, DOCTOR | Record a telemetry reading |
| `GET` | `/telemetria` | ADMIN, DOCTOR | List all telemetry data |
| `GET` | `/telemetria/{id}` | ADMIN, DOCTOR | Get a specific reading |
| `GET` | `/telemetria/tratamiento/{tratamientoId}` | ADMIN, DOCTOR | Readings by treatment |
| `GET` | `/telemetria/paciente/{pacienteId}` | ADMIN, DOCTOR | Readings by patient |
| `GET` | `/telemetria/me` | PACIENTE | Own telemetry data |
| `GET` | `/telemetria/mis-pacientes/{pacienteId}` | FAMILIAR | Readings for a linked patient |
| `GET` | `/telemetria/todos` | ADMIN | List all including deleted |
| `GET` | `/telemetria/eliminados` | ADMIN | List only soft-deleted |

---

### Alerts

Alerts and recommendations generated during a treatment.

| Method | Path | Roles | Description |
|---|---|---|---|
| `POST` | `/alertas` | ADMIN, DOCTOR | Create an alert or recommendation |
| `GET` | `/alertas` | ADMIN, DOCTOR | List all alerts |
| `GET` | `/alertas/{id}` | ADMIN, DOCTOR | Get alert by ID |
| `GET` | `/alertas/tratamiento/{tratamientoId}` | ADMIN, DOCTOR | Alerts for a treatment |
| `GET` | `/alertas/paciente/{pacienteId}` | ADMIN, DOCTOR | Alerts for a patient |
| `GET` | `/alertas/tipo/{tipo}` | ADMIN, DOCTOR | Filter by type (`ALERTA` or `RECOMENDACION`) |
| `GET` | `/alertas/me` | PACIENTE | Own alerts |
| `DELETE` | `/alertas/{id}` | ADMIN | Soft-delete an alert |
| `GET` | `/alertas/todos` | ADMIN | List all including deleted |
| `GET` | `/alertas/eliminados` | ADMIN | List only soft-deleted |

---

### Devices (Watches)

Smart watches assigned to patients for telemetry collection.

| Method | Path | Roles | Description |
|---|---|---|---|
| `POST` | `/relojes` | ADMIN | Register a new device |
| `GET` | `/relojes` | ADMIN, DOCTOR | List all devices |
| `GET` | `/relojes/{id}` | ADMIN, DOCTOR | Get device by ID |
| `PUT` | `/relojes/{id}` | ADMIN, DOCTOR | Update device data |
| `DELETE` | `/relojes/{id}` | ADMIN | Soft-delete a device |
| `GET` | `/relojes/disponibles` | ADMIN, DOCTOR | List available (unassigned) devices |
| `GET` | `/relojes/bateria-baja` | ADMIN, DOCTOR | List devices with low battery |
| `GET` | `/relojes/me` | PACIENTE | Get own assigned device |
| `GET` | `/relojes/mis-pacientes/{pacienteId}` | FAMILIAR | Get device of a linked patient |
| `GET` | `/relojes/todos` | ADMIN | List all including deleted |
| `GET` | `/relojes/eliminados` | ADMIN | List only soft-deleted |

---

## Data Models

### Enums

**Role**
```
ADMIN | DOCTOR | PACIENTE | FAMILIAR
```

**EstadoTratamiento** (Treatment status)
```
ACTIVO | FINALIZADO | CANCELADO
```

**EstadoReloj** (Device status)
```
DISPONIBLE | ASIGNADO | EN_MANTENIMIENTO
```

**Tipo** (Alert type)
```
ALERTA | RECOMENDACION
```

### Entity Relationships

```
Usuario (1) ─── (1) Paciente
Usuario (1) ─── (1) Doctor
Usuario (1) ─── (1) Familiar

Departamento (1) ─── (N) Paciente
Departamento (1) ─── (N) Doctor

Paciente (N) ─── (M) Familiar        [via Contacto join table]

Doctor      (1) ─┐
Paciente    (1) ─┤─── (1) Tratamiento
Reloj       (1) ─┘

Tratamiento (1) ─── (N) Telemetria
Tratamiento (1) ─── (N) Alerta
```

### Core Entity Fields

**Usuario**
- `id`, `email`, `password` (BCrypt), `role`, `foto_perfil_path`, `activo`

**Paciente**
- `id`, `nombre`, `apellido`, `telefono`, `documento`, `tarjeta_sanitaria`, `fecha_nacimiento`, `peso`, `altura`, linked to `Departamento` and list of `Familiar`

**Doctor**
- `id`, `nombre`, `apellido`, `num_colegiado`, linked to `Departamento`

**Familiar**
- `id`, `nombre`, `apellido`, `telefono`, `documento`, `tarjeta_sanitaria`

**Departamento**
- `id`, `nombre`, `centro`, `ubicacion`, `email`

**Reloj**
- `id`, `imei`, `mac_address`, `android_id`, `nivel_bateria`, `estado` (EstadoReloj)

**Tratamiento**
- `id`, `tipo_isotopo`, `dosis_inicial`, `fecha_administracion`, `fecha_fin`, `estado` (EstadoTratamiento), linked to `Paciente`, `Doctor`, `Reloj`

**Telemetria**
- `id`, `timestamp`, `frecuencia_cardiaca`, `pasos`, `temperatura`, `nivel_radiacion`, linked to `Tratamiento`

**Alerta**
- `id`, `tipo` (ALERTA/RECOMENDACION), `mensaje`, `timestamp`, linked to `Tratamiento`

---

## Project Structure

```
radioisotopo/
├── src/main/java/com/projecte/radioisotopo/
│   ├── Auth/                   # AuthController, AuthService, AuthRequest/Response
│   ├── Configuration/          # OpenApiConfig, FhirConfig
│   ├── Controller/             # One controller per resource
│   ├── DTO/                    # Input validation records (@NotBlank, @NotNull)
│   ├── Exception/              # GlobalExceptionHandler (unified error format)
│   ├── Model/                  # JPA entities + enums
│   ├── Repository/             # Spring Data JPA interfaces
│   ├── Security/               # JwtService, JwtAuthenticationFilter,
│   │                           #   SecurityConfiguration, ApplicationConfig, CorsConfig
│   └── Service/                # Business logic + FHIR conversion per resource
├── src/main/resources/
│   ├── application.properties  # Port, DB credentials, JWT secret, upload path
│   └── schema.sql              # Database schema (auto-executed on startup)
├── docker-compose.yml          # MySQL 8 container (port 3305)
└── pom.xml
```

### Key configuration values (`application.properties`)

| Property | Value |
|---|---|
| Server port | `8081` |
| Database | `db_radioIsotopo` on `localhost:3305` |
| DB user | `api_user` / `1234` |
| JWT expiry | 24 hours |
| Image upload path | `src/main/resources/static/imagenes/` |
| CORS origin | `http://localhost:5173` |

### Error Response Format

All errors return a consistent JSON body:

```json
{
  "timestamp": "2026-05-31T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Paciente not found with id: 99"
}
```

HTTP status codes used: `400` (validation), `401` (unauthenticated), `403` (forbidden), `404` (not found), `500` (server error).
