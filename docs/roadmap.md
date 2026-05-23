# Cosmic Engine Roadmap

Cosmic Engine is a long-term astrophysics simulation engine.

The project starts with simple 2D Newtonian mechanics and gradually grows into a more advanced educational and simulation platform.

## Stage 0 — Project Foundation

Goal: prepare a clean and professional project structure.

Scope:

- GitHub repository
- README
- Backend application
- Frontend application
- Health check endpoint
- Basic backend test
- Docker setup
- GitHub Actions CI
- Project documentation

Result:

A working project foundation with backend, frontend, Docker and CI.

Status: in progress

---

## Stage 1 - Material Point Physics

Goal: simulate the motion of a single body.

Physics topics:

- position
- velocity
- acceleration
- mass
- force
- Newton's second law

Minimal math:

```text
F = ma
a = F / m
v_new = v_old + a * dt
x_new = x_old + v * dt
```

Programming tasks:

- create Vector2D
- create Body
- create PhysicsEngine
- simulate a body moving in a straight line
- add unit tests

Result:

A point moves on the screen with configurable velocity and mass.

---

## Stage 2 - Newtonian Gravity

Goal: add gravitational attraction between two bodies.

Physics topics:

- Newton's law of universal gravitation
- inverse-square force
- gravitational force vector
- difference between real force and required orbital force

Minimal math:

```text
F = G * m1 * m2 / r^2
```

Programming tasks:

- create GravityCalculator
- calculate gravitational force
- add star and planet
- create a stable orbit
- add unit tests

Result:

A planet orbits a star.

---

## Stage 3 - N-body Simulation

Goal: simulate multiple bodies interacting gravitationally.

Physics topics:

- N-body problem
- sum of forces
- numerical stability
- time step errors

Programming tasks:

- multiple planets
- moons
- comets
- close passes
- collisions or simplified merging

Result:

A small custom planetary system.

---

## Stage 4 - Visualization and Interaction

Goal: make the simulation interactive and presentable.

Scope:

- zoom
- camera
- orbit trails
- pause/start/reset
- change mass
- change velocity
- add new bodies
- show speed, distance and energy charts

Result:

An application suitable for portfolio presentation.

---

## Stage 5 - Backend Persistence

Goal: add backend features useful for a real application.

Scope:

- users
- authentication
- saved simulations
- public/private simulations
- simulation history
- REST API
- WebSocket support for live simulation state

Result:

Users can save and replay their own planetary systems.

---

## Stage 6 - Better Numerical Physics

Goal: improve physical accuracy.

Physics topics:

- Euler integration
- Verlet integration
- Runge-Kutta methods
- total energy
- momentum conservation
- orbit stability

Programming tasks:

- compare integrators
- show energy error charts
- create educational mode explaining simulation drift

Result:

The project becomes a serious physics learning tool.

---

## Stage 7 - Black Holes

Goal: introduce simplified black hole simulations.

Physics topics:

- Schwarzschild radius
- event horizon
- escape velocity
- accretion disk
- gravitational lensing
- simplified relativistic effects

Programming tasks:

- create BlackHole body type
- calculate Schwarzschild radius
- visualize event horizon
- simulate bodies falling into a black hole
- add accretion disk visualization

Result:

An interactive black hole model.

---

## Stage 8 - Relativity Extensions

Goal: gradually introduce simplified general relativity concepts.

Physics topics:

- time dilation
- curved spacetime
- geodesics
- light near black holes
- gravitational lensing

Result:

The project expands from Newtonian mechanics into modern astrophysics.
