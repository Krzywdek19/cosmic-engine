package pl.exceptionhandled.cosmicengine.simulation.model;

import pl.exceptionhandled.cosmicengine.physics.model.Vector2D;

public record BodySimulationFrame(
        int step,
        double time,
        Vector2D position,
        Vector2D velocity,
        Vector2D acceleration
) {
}