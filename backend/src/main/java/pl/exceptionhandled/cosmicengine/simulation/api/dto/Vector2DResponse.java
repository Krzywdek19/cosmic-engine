package pl.exceptionhandled.cosmicengine.simulation.api.dto;

import pl.exceptionhandled.cosmicengine.physics.model.Vector2D;

public record Vector2DResponse(double x, double y) {

    public static Vector2DResponse from(Vector2D vector) {
        return new Vector2DResponse(vector.x(), vector.y());
    }
}