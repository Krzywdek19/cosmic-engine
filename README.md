# Cosmic Engine

Cosmic Engine is a long-term astrophysics simulation project.

The goal is to build an interactive 2D/3D simulation engine that starts with Newtonian mechanics and orbital motion, then gradually expands toward N-body simulations, black holes, relativistic effects and educational physics experiments.

## Project Goals

- Learn physics and astrophysics through implementation
- Build a portfolio-level programming project
- Create a clean simulation engine architecture
- Visualize orbital mechanics and physical quantities
- Add tests, documentation, CI/CD and deployment workflow

## Physics Model

The current backend uses a simplified Newtonian gravity simulation model.

At this stage:

- the most massive body is selected as the static central body,
- the central body does not move,
- other bodies are simulated relative to the central body,
- secondary bodies do not attract each other,
- the project does not yet implement full N-body simulation.

Current gravity simulation model:

```text
STATIC_CENTRAL_BODY
```

Current motion integrator:

```text
CONSTANT_ACCELERATION_STEP
```

More details are described in:

```text
backend/docs/physics-model.md
```

## First Milestone

Build a simple 2D simulation where a planet orbits a star using Newtonian gravity.

Scope:

- Vector2D
- Body
- PhysicsEngine
- NewtonianGravityCalculator
- MotionIntegrator
- ConstantAccelerationStepIntegrator
- SimulationLoop
- Basic frontend visualization
- Unit tests
- CI/CD pipeline

## Tech Stack

- Java 21
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
- Basic motion simulation
- Simple motion REST API
- Trajectory generation

### Stage 2 — Newtonian Gravity

- Newtonian gravitational force
- Static central body gravity model
- Star and planet trajectory simulation
- Gravity trajectory REST API
- Basic orbital motion visualization
- Preparation for future N-body simulation

### Stage 3 — N-body Simulation

- Multiple interacting bodies
- Moons, planets, comets
- Mutual gravitational interactions
- Numerical stability
- Energy and momentum checks

### Stage 4 — Visualization and Interaction

- Camera
- Zoom
- Orbit trails
- Pause/start/reset
- Editable body parameters
- Charts for velocity, energy and distance

### Stage 5 — Backend Persistence

- Saved simulations
- Users
- Public/private simulations
- REST API
- Simulation history

### Stage 6 — Better Numerical Physics

- Euler
- Verlet
- Runge-Kutta
- Energy conservation
- Integrator comparison

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

The backend foundation is complete and the project is currently focused on Stage 2 — Newtonian Gravity.

Completed:

- project repository
- README
- backend Spring Boot app
- health check endpoint
- backend tests
- frontend React app
- Docker setup
- GitHub Actions CI
- Maven Wrapper
- Java 21 standardization
- global API error handling
- request safety limits
- Vector2D model
- Body model
- PhysicsEngine
- NewtonianGravityCalculator
- configurable gravitational constant
- MotionIntegrator abstraction
- ConstantAccelerationStepIntegrator
- SimulationLoop
- SimpleMotionSimulationService
- static central gravity trajectory endpoint
- gravity simulation metadata in API responses
- physics model documentation

Current simulation model:

```text
STATIC_CENTRAL_BODY
```

Current integrator:

```text
CONSTANT_ACCELERATION_STEP
```

Next goals:

- improve domain validation for Body
- add clearer simulation response frames
- prepare the codebase for full N-body simulation
- add better numerical integrators such as Euler, Verlet and Runge-Kutta