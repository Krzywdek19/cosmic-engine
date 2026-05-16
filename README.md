# Cosmic Engine

Cosmic Engine is a long-term astrophysics simulation project.

The goal is to build an interactive 2D/3D simulation engine that starts with Newtonian mechanics and orbital motion, then gradually expands toward N-body simulations, black holes, relativistic effects and educational physics experiments.

## Project Goals

- Learn physics and astrophysics through implementation
- Build a portfolio-level programming project
- Create a clean simulation engine architecture
- Visualize orbital mechanics and physical quantities
- Add tests, documentation, CI/CD and deployment workflow

## First Milestone

Build a simple 2D simulation where a planet orbits a star using Newtonian gravity.

Scope:

- Vector2D
- Body
- PhysicsEngine
- GravityCalculator
- SimulationLoop
- Basic frontend visualization
- Unit tests
- CI/CD pipeline

## Tech Stack

- Java
- Spring Boot
- React
- Canvas API
- Docker
- GitHub Actions

## Getting Started

### Requirements

To run the project locally, you need:

- Java 21
- Maven Wrapper included in the backend project
- Node.js 22
- Docker and Docker Compose

If the backend was generated with Java 17, use Java 17 instead of Java 21.

---

## Running Backend Locally

Go to the backend directory:

```bash
cd backend
```

Run the Spring Boot application:

```bash
./mvnw spring-boot:run
```

On Windows:

```bash
mvnw.cmd spring-boot:run
```

Backend should be available at:

```text
http://localhost:8080
```

Health check endpoint:

```text
GET http://localhost:8080/api/v1/health
```

Expected response:

```json
{
  "status": "UP",
  "service": "cosmic-engine-backend"
}
```

---

## Running Frontend Locally

Go to the frontend directory:

```bash
cd frontend
```

Install dependencies:

```bash
npm install
```

Run the frontend development server:

```bash
npm run dev
```

Frontend should be available at:

```text
http://localhost:5173
```

---

## Running with Docker Compose

From the root directory:

```bash
docker compose up --build
```

Backend:

```text
http://localhost:8080/api/v1/health
```

Frontend:

```text
http://localhost:3000
```

To stop containers:

```bash
docker compose down
```

---

## Running Tests

### Backend Tests

```bash
cd backend
./mvnw test
```

On Windows:

```bash
cd backend
mvnw.cmd test
```

### Frontend Build Check

```bash
cd frontend
npm run build
```

---

## Continuous Integration

The project uses GitHub Actions.

Current CI pipeline runs on every push and pull request to the `main` branch.

The pipeline checks:

- backend tests
- frontend production build

Workflow file:

```text
.github/workflows/ci.yml
```

---

## Roadmap

### Stage 0 — Project Foundation

- Repository structure
- README
- Basic backend health check
- Basic frontend screen
- Docker Compose
- GitHub Actions CI

### Stage 1 — Material Point Physics

- Position
- Velocity
- Acceleration
- Force
- Mass
- Newton's second law

### Stage 2 — Newtonian Gravity

- Gravitational force
- Star and planet simulation
- Stable orbit

### Stage 3 — N-body Simulation

- Multiple interacting bodies
- Moons, planets, comets
- Numerical stability

### Stage 4 — Visualization and Interaction

- Camera
- Zoom
- Orbit trails
- Pause/start/reset
- Editable body parameters

### Stage 5 — Backend Persistence

- Saved simulations
- Users
- Public/private simulations
- REST API

### Stage 6 — Better Numerical Physics

- Euler
- Verlet
- Runge-Kutta
- Energy conservation

### Stage 7 — Black Holes

- Schwarzschild radius
- Event horizon
- Accretion disk
- Simplified relativistic effects

### Stage 8 — Relativity Extensions

- Time dilation
- Geodesics
- Light bending
- Gravitational lensing

---

## Current Status

Stage 0 is focused on project foundation.

Completed:

- project repository
- README
- backend Spring Boot app
- health check endpoint
- backend test
- frontend React app
- Docker setup
- GitHub Actions CI
- roadmap documentation
- architecture documentation

Next stage:

```text
Stage 1 — Material Point Physics
```

The next goal is to implement the first physics model:

- Vector2D
- Body
- PhysicsEngine
- simulation step
- unit tests for movement
