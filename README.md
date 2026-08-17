# TransitOps

### Smart Transport Operations Platform

TransitOps is a full-stack transport operations platform built with **Java, Spring Boot, React, and PostgreSQL**.

It manages vehicles, drivers, trips, maintenance, fuel, expenses, and operational analytics through a centralized web application.

The project focuses on **role-based access control, backend business-rule validation, automated resource status transitions, REST APIs, and operational reporting**.

## Live Demo

**Frontend:**
https://transitops-frontend-bgrs.onrender.com

**Backend API:**
https://transitops-backend-i2ck.onrender.com

> The application is deployed on Render. Free instances may take a few seconds to wake after inactivity.

## Demo Accounts

| Role              | Email                    | Password      |
| ----------------- | ------------------------ | ------------- |
| Fleet Manager     | `fleet@transitops.com`   | `Fleet@123`   |
| Driver            | `driver@transitops.com`  | `Driver@123`  |
| Safety Officer    | `safety@transitops.com`  | `Safety@123`  |
| Financial Analyst | `finance@transitops.com` | `Finance@123` |

## Key Features

* JWT-based authentication
* Role-Based Access Control using Spring Security
* Vehicle and driver management
* Trip creation, dispatch, completion, and cancellation
* Backend dispatch validation
* Automatic vehicle and driver status transitions
* Maintenance and fuel tracking
* Expense management
* Dashboard KPIs and charts
* Search and filtering
* Vehicle operational analytics
* CSV report export
* PostgreSQL persistence
* RESTful backend APIs
* Responsive React frontend

## Role-Based Access

### Fleet Manager

* Manage vehicles
* Manage fleet operations
* Manage maintenance
* Manage trips

### Driver

* Access trip-related operations
* View assigned operational information

### Safety Officer

* Manage driver compliance information
* Monitor license and safety data

### Financial Analyst

* Manage expenses and fuel costs
* View operational cost and financial analytics

## Core Business Rules

The backend enforces important transport operation rules instead of relying only on frontend validation.

* Retired vehicles cannot be dispatched
* Vehicles under maintenance cannot be dispatched
* Vehicles already on a trip cannot be reassigned
* Expired driver licenses prevent trip assignment
* Suspended drivers cannot be assigned
* Drivers already on a trip cannot be reassigned
* Cargo weight cannot exceed vehicle capacity
* Only `DRAFT` trips can be dispatched

### Dispatch Lifecycle

```text
DRAFT
  ↓
DISPATCHED
  ↓
COMPLETED
```

or

```text
DISPATCHED
  ↓
CANCELLED
```

When a trip is dispatched:

```text
Trip    → DISPATCHED
Vehicle → ON_TRIP
Driver  → ON_TRIP
```

When completed or cancelled:

```text
Vehicle → AVAILABLE
Driver  → AVAILABLE
```

### Maintenance Lifecycle

```text
AVAILABLE
    ↓
IN_SHOP
    ↓
AVAILABLE
```

Vehicles in `IN_SHOP` status are automatically prevented from being dispatched.

## Backend Engineering

The dispatch workflow performs backend validation for:

* Trip state
* Vehicle availability
* Driver availability
* Driver license validity
* Cargo capacity

Transactional service operations maintain consistent resource states and prevent unavailable vehicles or drivers from being assigned to multiple trips.

## Analytics & Reporting

Vehicle analytics include:

* Completed distance
* Fuel consumption
* Fuel efficiency
* Fuel cost
* Maintenance cost
* Other expenses
* Operational cost
* Revenue
* Vehicle ROI

Reports can be exported as **CSV files** for spreadsheet-based analysis.

## Technology Stack

### Backend

* Java 21
* Spring Boot 4
* Spring Security
* JWT
* Spring Data JPA
* Hibernate
* PostgreSQL
* Maven

### Frontend

* React
* Vite
* React Router
* Axios
* Recharts
* Lucide React

### Deployment

* Render Static Site — React frontend
* Render Docker Web Service — Spring Boot backend
* PostgreSQL — Database

## Architecture

```text
React + Vite
     │
     │ Axios / REST API
     ▼
Spring Boot
     │
Spring Security
     │
JWT Authentication
     ▼
Controllers
     ▼
Services
     ▼
Spring Data JPA
     ▼
PostgreSQL
```

The frontend communicates with the backend through REST APIs. JWT tokens are attached to authenticated requests, while Spring Security handles backend authorization.

## Project Structure

```text
transitops/
├── backend/
│   ├── src/
│   ├── pom.xml
│   └── Dockerfile
│
├── frontend/
│   ├── src/
│   │   ├── api/
│   │   ├── components/
│   │   ├── context/
│   │   ├── pages/
│   │   └── utils/
│   └── package.json
│
└── README.md
```

## Run Locally

### Prerequisites

* Java 21
* Node.js
* PostgreSQL
* Git

### 1. Create Database

```sql
CREATE DATABASE transitops;
```

Configure the PostgreSQL connection in:

```text
backend/src/main/resources/application.properties
```

### 2. Start Backend

Windows:

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

Linux/macOS:

```bash
cd backend
./mvnw spring-boot:run
```

Backend:

```text
http://localhost:8080
```

### 3. Start Frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend:

```text
http://localhost:5173
```

## Security

TransitOps implements:

* JWT authentication
* Spring Security authorization
* BCrypt password hashing
* Role-based endpoint protection
* Protected React routes
* Stateless authentication
* Production CORS configuration

## Project Highlights

This project demonstrates practical experience with:

* Full-stack application development
* Java and Spring Boot
* REST API design
* Spring Security and JWT
* Role-Based Access Control
* JPA/Hibernate
* PostgreSQL database design
* Backend business-rule validation
* Transactional workflows
* React frontend development
* Search and filtering
* Operational analytics
* CSV reporting
* Docker-based deployment

## Repository

https://github.com/sharankumar-k/transitops

## Author

**Sharan Kumar K**

GitHub:
https://github.com/sharankumar-k
