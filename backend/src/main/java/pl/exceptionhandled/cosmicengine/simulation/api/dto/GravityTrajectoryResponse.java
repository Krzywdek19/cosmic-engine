package pl.exceptionhandled.cosmicengine.simulation.api.dto;

import java.util.List;

public record GravityTrajectoryResponse(
        int attractingBodyIndex,
        Vector2DResponse attractingBodyPosition,
        List<BodyTrajectoryResponse> trajectories
) {
}