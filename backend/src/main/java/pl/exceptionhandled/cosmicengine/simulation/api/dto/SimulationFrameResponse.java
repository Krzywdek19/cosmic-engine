package pl.exceptionhandled.cosmicengine.simulation.api.dto;

public record SimulationFrameResponse(
        int step,
        double time,
        Vector2DResponse position,
        Vector2DResponse velocity
) {
}