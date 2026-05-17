package pl.exceptionhandled.cosmicengine.simulation.api.dto;

import java.util.List;

public record SimpleMotionTrajectoryResponse(
        double mass,
        Vector2DResponse initialPosition,
        Vector2DResponse appliedForce,
        Vector2DResponse acceleration,
        Vector2DResponse finalPosition,
        Vector2DResponse finalVelocity,
        int steps,
        double deltaTime,
        List<SimulationFrameResponse> frames
) {
}