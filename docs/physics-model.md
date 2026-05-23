# Physics model

## Current simulation model

The current gravity simulation uses a simplified static central body model.

In this model:

- the most massive body is selected as the central body,
- the central body is treated as static,
- every other body is simulated relative to the central body,
- secondary bodies do not attract each other,
- the central body is not affected by the smaller bodies.

This is not a full N-body simulation yet.

## Gravity model

The project currently uses Newtonian gravity:

```text
F = G * m1 * m2 / r^2
```

Where:

- `F` is the gravitational force,
- `G` is the gravitational constant,
- `m1` and `m2` are body masses,
- `r` is the distance between bodies.

The force direction is calculated from the affected body toward the attracting body.

## Integrator

The current motion update uses a constant acceleration step:

```text
x_new = x_old + v * dt + 0.5 * a * dt^2
v_new = v_old + a * dt
```

Within a single step, acceleration is treated as constant.

This is an approximation. In gravity simulations, acceleration changes as the distance between bodies changes.

## Units

The backend currently uses normalized simulation units.

By default:

```text
G = 1.0
```

This makes early educational simulations easier to visualize.

Real SI gravity would use:

```text
G = 6.67430e-11
```

However, real SI values require realistic masses, distances and time scales, which are not convenient for the current visual simulation stage.

## Current limitations

The current model does not yet include:

- full N-body interactions,
- mutual attraction between all bodies,
- collision detection,
- energy conservation checks,
- momentum conservation checks,
- advanced numerical integrators,
- relativistic effects.

These features are planned for later stages.