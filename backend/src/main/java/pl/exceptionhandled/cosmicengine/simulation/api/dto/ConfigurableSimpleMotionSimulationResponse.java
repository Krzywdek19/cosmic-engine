package pl.exceptionhandled.cosmicengine.simulation.api.dto;

public record ConfigurableSimpleMotionSimulationResponse(
        double mass,
        Vector2DResponse initialPosition,
        Vector2DResponse appliedForce,
        Vector2DResponse acceleration,
        Vector2DResponse finalPosition,
        Vector2DResponse finalVelocity,
        int steps,
        double deltaTime
) {
}