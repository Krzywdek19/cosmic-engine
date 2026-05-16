# Cosmic Engine Architecture

## Overview

Cosmic Engine is planned as a modular astrophysics simulation project.

The initial version is a monorepo containing:

- backend
- frontend
- documentation
- Docker setup
- CI configuration

The project starts simple and grows gradually. The first goal is not a large distributed system, but a working simulation foundation.

---

## Repository Structure

```text
cosmic-engine/
├── backend/
├── frontend/
├── docs/
├── .github/
├── docker-compose.yml
└── README.md
```

---

## Backend

Technology:

- Java
- Spring Boot
- Maven

Base package:

```text
pl.exceptionhandled.cosmicengine
```

Current responsibility:

- expose health check endpoint
- provide future API for simulations
- later handle saving and loading simulation configurations

Current endpoint:

```text
GET /api/v1/health
```

Response:

```json
{
  "status": "UP",
  "service": "cosmic-engine-backend"
}
```

Planned backend modules/packages:

```text
pl.exceptionhandled.cosmicengine.health
pl.exceptionhandled.cosmicengine.physics
pl.exceptionhandled.cosmicengine.simulation
pl.exceptionhandled.cosmicengine.body
pl.exceptionhandled.cosmicengine.gravity
```

Later possible modules:

```text
pl.exceptionhandled.cosmicengine.auth
pl.exceptionhandled.cosmicengine.users
pl.exceptionhandled.cosmicengine.persistence
pl.exceptionhandled.cosmicengine.blackhole
```

---

## Frontend

Technology:

- React
- TypeScript
- Vite
- Canvas API in the first simulation milestone

Current responsibility:

- show initial project screen

Future responsibility:

- render bodies
- render orbits
- control simulation
- display physical values
- display charts
- allow users to create and edit simulations

---

## Physics Engine Direction

The physics engine should start as a simple and testable core.

Initial concepts:

- Vector2D
- Body
- Force
- PhysicsEngine
- GravityCalculator
- SimulationLoop

Important rule:

The physics core should not depend on the frontend.

The frontend should visualize simulation results, not contain the main physics logic.

---

## Data Flow

Initial planned data flow:

```text
User input
   ↓
Frontend controls
   ↓
Simulation state
   ↓
Physics engine update
   ↓
New positions and velocities
   ↓
Canvas rendering
```

Later backend-supported flow:

```text
Frontend
   ↓
REST API
   ↓
Backend
   ↓
Database
```

---

## Testing Strategy

Current tests:

- backend health check test

Planned tests:

- Vector2D operations
- force calculations
- motion updates
- gravity calculations
- stable orbit approximation
- simulation step correctness

The first physics tests should focus on simple, deterministic values.

---

## Development Rules

1. Build a working model first.
2. Keep physics understandable.
3. Add tests for every important formula.
4. Keep frontend and physics logic separated.
5. Improve accuracy after the basic version works.
6. Document every major physical concept.
7. Avoid unnecessary architecture before the simulation works.

---

## First Milestone Architecture

The first milestone should contain:

```text
frontend visualization
        ↓
simulation loop
        ↓
physics engine
        ↓
gravity calculator
        ↓
bodies and vectors
```

Goal:

A planet orbits a star because of Newtonian gravity.
