package pl.exceptionhandled.cosmicengine.simulation.api.dto;

import java.util.List;

public record BodyTrajectoryResponse(
        int bodyIndex,
        double mass,
        List<TrajectoryFrameResponse> frames
) {
}