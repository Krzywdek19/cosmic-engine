package pl.exceptionhandled.cosmicengine.simulation.api.dto;

public record SimpleMotionSimulationResponse(
        Vector2DResponse initialPosition,
        Vector2DResponse finalPosition,
        Vector2DResponse finalVelocity,
        int steps,
        double deltaTime
) {
}