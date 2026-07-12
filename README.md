@'
# TransitOps

Smart Transport Operations Platform built for Odoo Hackathon 2026.

TransitOps is a full-stack fleet and transport operations management system that manages vehicles, drivers, trips, maintenance, expenses, fuel logs, operational dashboards, analytics, and role-based access control.

## Tech Stack

### Backend
- Java 21
- Spring Boot 4
- Spring Security
- JWT Authentication
- Spring Data JPA
- Hibernate
- PostgreSQL
- Maven

### Frontend
- React
- Vite
- React Router
- Axios
- Recharts
- Lucide React

## Architecture

The backend follows a layered architecture:

Controller -> Service -> Repository -> PostgreSQL

The React frontend communicates with REST APIs through Axios. JWT tokens are attached to authenticated API requests.

## Core Modules

- Authentication and Role-Based Access Control
- Vehicle Management
- Driver Management
- Trip and Dispatch Management
- Maintenance Management
- Fuel Logging
- Expense Management
- Operations Dashboard
- Vehicle Analytics
- CSV Report Export

## Roles

TransitOps supports:

- FLEET_MANAGER
- DISPATCHER
- SAFETY_OFFICER
- FINANCIAL_ANALYST

Backend authorization is enforced using Spring Security method-level authorization.

## Business Rules

### Trip Dispatch

A trip can be dispatched only when:

- Trip status is DRAFT
- Vehicle status is AVAILABLE
- Driver status is AVAILABLE
- Cargo weight does not exceed vehicle capacity
- Driver license is valid

When dispatched:

- Trip becomes DISPATCHED
- Vehicle becomes ON_TRIP
- Driver becomes ON_TRIP
- Vehicle odometer is captured as the trip start odometer

### Trip Completion

When a trip is completed:

- Trip becomes COMPLETED
- Vehicle odometer is updated
- Vehicle returns to AVAILABLE
- Driver returns to AVAILABLE
- Fuel information can be recorded automatically

### Maintenance

When maintenance starts:

- Maintenance becomes ACTIVE
- Vehicle becomes IN_SHOP
- IN_SHOP vehicles cannot be dispatched

When maintenance closes:

- Maintenance becomes CLOSED
- Maintenance cost is recorded
- Vehicle returns to AVAILABLE

## Concurrency-Safe Dispatch

TransitOps validates trip, vehicle, and driver state before dispatch.

The dispatch workflow prevents repeated dispatch and unavailable vehicle or driver assignment. Transactional service operations maintain consistent resource state during trip lifecycle transitions.

## Analytics

Vehicle analytics include:

- Completed Distance
- Fuel Consumption
- Fuel Efficiency
- Fuel Cost
- Maintenance Cost
- Other Expenses
- Operational Cost
- Revenue
- Vehicle ROI

### Formulas

Fuel Efficiency:

`Completed Distance / Fuel Liters`

Operational Cost:

`Fuel Cost + Maintenance Cost`

Vehicle ROI:

`((Revenue - Fuel Cost - Maintenance Cost) / Acquisition Cost) * 100`

## Demo Workflow

A verified workflow includes:

1. Create Van-05 with 500 kg maximum capacity.
2. Register driver Alex with a valid license.
3. Create a 450 kg trip.
4. Dispatch the trip.
5. Vehicle and driver automatically move to ON_TRIP.
6. Repeated dispatch is rejected.
7. Complete the trip and update the final odometer.
8. Vehicle and driver automatically return to AVAILABLE.
9. Fuel usage is automatically recorded.
10. Start maintenance.
11. Vehicle moves to IN_SHOP.
12. Dispatch using the IN_SHOP vehicle is rejected.
13. Close maintenance and record maintenance cost.
14. Vehicle returns to AVAILABLE.
15. Analytics and CSV reports reflect operational data.

## API Overview

### Authentication
- `POST /api/auth/login`

### Dashboard
- `GET /api/dashboard`

### Vehicles
- `/api/vehicles`

### Drivers
- `/api/drivers`

### Trips
- `/api/trips`
- `POST /api/trips/{id}/dispatch`
- `POST /api/trips/{id}/complete`
- `POST /api/trips/{id}/cancel`

### Maintenance
- `/api/maintenance`
- `POST /api/maintenance/{id}/close`

### Fuel Logs
- `/api/fuel-logs`

### Expenses
- `/api/expenses`

### Reports
- `GET /api/reports/vehicle/{vehicleId}`
- `GET /api/reports/vehicles/csv`

## Local Setup

### Prerequisites

- Java 21
- Node.js
- PostgreSQL

### Database

Create a PostgreSQL database named:

`transitops`

Configure database credentials in:

`backend/src/main/resources/application.properties`

### Run Backend

```bash
cd backend
./mvnw spring-boot:run