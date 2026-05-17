# Stage 1 - Material Point Simulation

## Status

Stage 1 is implemented.

The project now contains the first basic physics model for simulating the motion of a single body in 2D space.

Implemented elements:

- `Vector2D`
- `Body`
- `PhysicsEngine`
- `SimulationLoop`
- force application
- simple motion update
- fixed demo simulation endpoint
- configurable simulation endpoint
- unit tests
- API integration test

## Goal

The goal of this stage was to simulate the motion of one physical body treated as a material point.

A material point is a simplified physical object where size and shape are ignored. Only the following properties matter:

- mass
- position
- velocity
- acceleration
- applied force

This simplification is enough for the first version of the engine.

## Physical Model

The simulation is based on Newton's second law:

```text
F = m * a
```

After rearranging:

```text
a = F / m
```

Where:

- `F` is force
- `m` is mass
- `a` is acceleration

The engine uses this relation to calculate acceleration from the force applied to a body.

## Motion Update

For one simulation step, the engine updates position and velocity using:

```text
v_new = v_old + a * dt
```

```text
x_new = x_old + v_old * dt + 1/2 * a * dt^2
```

Where:

- `v_old` is the velocity before the update
- `v_new` is the velocity after the update
- `x_old` is the position before the update
- `x_new` is the position after the update
- `a` is acceleration
- `dt` is delta time

This version takes into account that velocity changes gradually during the time step.

## Vector2D

`Vector2D` represents a two-dimensional vector.

It is used for:

- position
- velocity
- acceleration
- force

Implemented operations:

- addition
- subtraction
- multiplication by scalar
- division by scalar
- magnitude calculation
- normalization

Example:

```java
Vector2D velocity = new Vector2D(10, 0);
```

This means that the body moves along the X axis.

## Body

`Body` represents a physical object in the simulation.

Current fields:

```text
mass
position
velocity
acceleration
```

The body validates that mass must be greater than zero.

## PhysicsEngine

`PhysicsEngine` is responsible for applying physical rules to a body.

Current responsibilities:

- applying force to a body
- calculating acceleration from force and mass
- updating velocity
- updating position

Main flow:

```text
force -> acceleration -> velocity -> position
```

## SimulationLoop

`SimulationLoop` runs multiple simulation steps.

Example:

```text
deltaTime = 1.0
steps = 5
```

This means that the simulation advances by 5 seconds in 5 separate steps.

The loop repeatedly calls `PhysicsEngine.update(...)`.

## API Endpoints

### Fixed simple motion simulation

```http
GET /api/v1/simulations/simple-motion
```

This endpoint runs a predefined simple motion simulation.

Example response:

```json
{
  "initialPosition": {
    "x": 0.0,
    "y": 0.0
  },
  "finalPosition": {
    "x": 50.0,
    "y": 0.0
  },
  "finalVelocity": {
    "x": 10.0,
    "y": 0.0
  },
  "steps": 5,
  "deltaTime": 1.0
}
```

### Configurable simple motion simulation

```http
POST /api/v1/simulations/simple-motion
```

Example request:

```json
{
  "mass": 2.0,
  "position": {
    "x": 0.0,
    "y": 0.0
  },
  "velocity": {
    "x": 0.0,
    "y": 0.0
  },
  "force": {
    "x": 10.0,
    "y": 0.0
  },
  "deltaTime": 2.0,
  "steps": 1
}
```

Expected physical calculation:

```text
a = F / m = 10 / 2 = 5
v = 0 + 5 * 2 = 10
x = 0 + 0 * 2 + 1/2 * 5 * 2^2 = 10
```

Example response:

```json
{
  "mass": 2.0,
  "initialPosition": {
    "x": 0.0,
    "y": 0.0
  },
  "appliedForce": {
    "x": 10.0,
    "y": 0.0
  },
  "acceleration": {
    "x": 5.0,
    "y": 0.0
  },
  "finalPosition": {
    "x": 10.0,
    "y": 0.0
  },
  "finalVelocity": {
    "x": 10.0,
    "y": 0.0
  },
  "steps": 1,
  "deltaTime": 2.0
}
```

## Validation

The configurable endpoint validates input data.

Current validation rules:

- mass must be positive
- delta time must be positive
- steps must be positive
- position must not be null
- velocity must not be null
- force must not be null
- vector coordinates must not be null

Invalid input should return HTTP `400 Bad Request`.

## Tests

Current test coverage includes:

### Vector2D tests

- adding vectors
- subtracting vectors
- multiplying by scalar
- dividing by scalar
- division by zero
- calculating magnitude
- normalizing a vector
- normalizing a zero vector

### Body tests

- creating a body
- rejecting zero mass
- rejecting negative mass
- updating mutable fields

### PhysicsEngine tests

- updating position using velocity
- updating velocity using acceleration
- applying force to a body
- moving a body after applying force
- rejecting invalid delta time

### SimulationLoop tests

- running a given number of simulation steps
- rejecting invalid step count

### API tests

- running the configurable simple motion endpoint
- checking returned acceleration
- checking returned final velocity
- checking returned final position
- rejecting invalid mass