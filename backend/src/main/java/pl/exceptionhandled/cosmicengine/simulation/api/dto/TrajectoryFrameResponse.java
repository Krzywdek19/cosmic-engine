package pl.exceptionhandled.cosmicengine.simulation.api.dto;

public record TrajectoryFrameResponse(
        int step,
        double time,
        Vector2DResponse position
) {
}